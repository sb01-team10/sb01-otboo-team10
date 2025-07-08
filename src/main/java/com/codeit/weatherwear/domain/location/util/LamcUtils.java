package com.codeit.weatherwear.domain.location.util;

import org.springframework.stereotype.Component;

@Component
public class LamcUtils {

  // Default 값 -> API 문서에 있는 내용 참고
  private static final double DEFAULT_GRID = 5.0;
  private static final double DEG_TO_RAD = Math.PI / 180.0;
  private static final double RAD_TO_DEG = 180.0 / Math.PI;
  private static final double GRID_OFFSET = 1.5;

  private static final LamcParameter DEFAULT_PARAMS = LamcParameter.builder()
      .re(6371.00877)
      .grid(DEFAULT_GRID)
      .slat1(30.0)
      .slat2(60.0)
      .olon(126.0)
      .olat(38.0)
      .xo(210.0 / DEFAULT_GRID)
      .yo(675.0 / DEFAULT_GRID)
      .build();

  // 격자값
  public record GridPoint(int nx, int ny) {

  }

  // 위경도
  public record GeoPoint(double longitude, double latitude) {

  }

  /**
   * 위경도 -> 격자 좌표
   */
  public GridPoint convertToGrid(double lat, double lon) {
    double[] result = lamcProj(lon, lat, 0, DEFAULT_PARAMS);
    return new GridPoint((int) Math.round(result[0]), (int) Math.round(result[1]));
  }

  /**
   * 격자 좌표 -> 위경도
   */
  public GeoPoint convertToGeo(int x, int y) {
    double[] result = lamcProj(x, y, 1, DEFAULT_PARAMS);
    return new GeoPoint(result[0], result[1]);
  }

  /**
   * Lambert Conformal Conic projection<br>기상청 API 문서에 나와 있는 변환 로직 JAVA에 맞게 수정하여 사용
   *
   * @param arg1 경도 or X
   * @param arg2 위도 or Y
   * @param code 0: geo → grid, 1: grid → geo
   */
  private static double[] lamcProj(double arg1, double arg2, int code, LamcParameter param) {
    double re = param.getRe() / param.getGrid();
    double slat1 = param.getSlat1() * DEG_TO_RAD;
    double slat2 = param.getSlat2() * DEG_TO_RAD;
    double olon = param.getOlon() * DEG_TO_RAD;
    double olat = param.getOlat() * DEG_TO_RAD;
    double xo = param.getXo();
    double yo = param.getYo();

    double sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) /
        Math.log(Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5));
    double sf = Math.pow(Math.tan(Math.PI * 0.25 + slat1 * 0.5), sn) * Math.cos(slat1) / sn;
    double ro = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + olat * 0.5), sn);

    if (code == 0) {
      // geo → grid
      double ra = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + arg2 * DEG_TO_RAD * 0.5), sn);
      double theta = arg1 * DEG_TO_RAD - olon;
      if (theta > Math.PI) {
        theta -= 2.0 * Math.PI;
      }
      if (theta < -Math.PI) {
        theta += 2.0 * Math.PI;
      }
      theta *= sn;

      double x = ra * Math.sin(theta) + xo + GRID_OFFSET;
      double y = ro - ra * Math.cos(theta) + yo + GRID_OFFSET;
      return new double[]{x, y};

    } else {
      // grid → geo
      double xn = arg1 - xo - GRID_OFFSET;
      double yn = ro - (arg2 - yo - GRID_OFFSET);
      double ra = Math.sqrt(xn * xn + yn * yn);
      double alat = 2.0 * Math.atan(Math.pow(re * sf / ra, 1.0 / sn)) - Math.PI * 0.5;

      double theta;
      if (Math.abs(xn) <= 1e-7) {
        theta = 0.0;
      } else if (Math.abs(yn) <= 1e-7) {
        theta = (xn > 0.0) ? Math.PI * 0.5 : -Math.PI * 0.5;
      } else {
        theta = Math.atan2(xn, yn);
      }

      double alon = theta / sn + olon;
      return new double[]{alon * RAD_TO_DEG, alat * RAD_TO_DEG};
    }
  }
}
