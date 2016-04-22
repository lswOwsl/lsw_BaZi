package lsw.liuyao.common;

import java.util.Random;

/**
 * Created by swli on 4/22/2016.
 */
public class HexagramTools {

    public int[] createHexagramByShiCao()
    {
        int[] lines = new int[6];

        for (int i=0;i<lines.length; i++)
        {
            lines[i] = createOneLine();
        }

        return lines;
    }

    public int createOneLine()
    {
        int sum = 50;
        int taijiSum = sum - 1;

        int firstBian = yiBian(taijiSum);
        int secondBian = yiBian(taijiSum-firstBian);
        int thirdBian = yiBian(taijiSum-firstBian-secondBian);

        int yao = (taijiSum - firstBian - secondBian - thirdBian)/4 ;

        return yao;
    }

    private int yiBian(int sum)
    {
        Random random = new Random();
        //因为nextInt 取出来的数十包括0的
        //右手最少得挂1个，所以用49-2了
        int left = random.nextInt(sum-2) + 1;
        int right = sum - left;
        int rightGua1 = 1;

        int leftYu = left%4 == 0 ? 4 : left%4;
        int rightYu = (right-rightGua1)%4 == 0 ? 4: (right-rightGua1)%4;

        int yuSum = leftYu + rightYu + rightGua1;

        return yuSum;
    }


}
