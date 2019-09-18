/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * <功能描述/>
 *
 * @author zhanghr
 * @date 2018年12月13日 上午11:14:44
 * @version 1.0
 */
public class VerifyCodeUtils {

  private static int width = 60;// 定义图片的width
  private static int height = 40;// 定义图片的height
  private static int codeCount = 4;// 定义图片上显示验证码的个数
  private static int xx = 10;
  private static int fontHeight = 16;
  private static int codeY = 25;
  private static int LINE_COUNT = 10;
  private static Font font[] = new Font[5];
  private static char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'Q', 'P',
      'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};

  static {
    font[0] = new Font("Ravie", Font.BOLD, fontHeight);
    font[1] = new Font("Antique Olive Compact", Font.BOLD, fontHeight);
    font[2] = new Font("Fixedsys", Font.BOLD, fontHeight);
    font[3] = new Font("Wide Latin", Font.BOLD, fontHeight);
    font[4] = new Font("Gill Sans Ultra Bold", Font.BOLD, fontHeight);
  }
  /**
   * 产生随机字体
   */
  private static Font getFont() {
    Random random = new Random();
    return font[random.nextInt(5)];
  }

  /**
   * 生成一个map集合 code为生成的验证码 codePic为生成的验证码BufferedImage对象
   * 
   * @return
   */
  public static Map<String, Object> generateCodeAndPic() {
    // 定义图像buffer
    BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    // Graphics2D gd = buffImg.createGraphics();
    // Graphics2D gd = (Graphics2D) buffImg.getGraphics();
    Graphics gd = buffImg.getGraphics();
    // 创建一个随机数生成器类
    Random random = new Random();
    // 将图像填充为白色
    gd.setColor(Color.WHITE);
    gd.fillRect(0, 0, width, height);

    // 随机设置当前验证码的字符的字体
    gd.setFont(getFont());
    // 画边框。
    gd.setColor(Color.WHITE);
    gd.drawRect(0, 0, width - 1, height - 1);

    // 随机产生多条干扰线，使图象中的认证码不易被其它程序探测到。
    gd.setColor(Color.BLACK);
    for (int i = 0; i < LINE_COUNT; i++) {
      int x = random.nextInt(width);
      int y = random.nextInt(height);
      int xl = random.nextInt(10);
      int yl = random.nextInt(10);
      gd.drawLine(x, y, x + xl, y + yl);
    }

    // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
    StringBuffer randomCode = new StringBuffer();
    int red = 0, green = 0, blue = 0;

    // 随机产生codeCount数字的验证码。
    for (int i = 0; i < codeCount; i++) {
      // 得到随机产生的验证码数字。
      String code = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
      // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
      red = random.nextInt(255);
      green = random.nextInt(255);
      blue = random.nextInt(255);

      // 用随机产生的颜色将验证码绘制到图像中。
      gd.setColor(new Color(red, green, blue));
      gd.drawString(code, (i + 1) * xx, codeY);

      // 将产生的四个随机数组合在一起。
      randomCode.append(code);
    }
    Map<String, Object> map = new HashMap<String, Object>();
    // 存放验证码
    map.put("code", randomCode.toString());
    // 存放生成的验证码BufferedImage对象
    map.put("img", buffImg);
    return map;
  }

}
