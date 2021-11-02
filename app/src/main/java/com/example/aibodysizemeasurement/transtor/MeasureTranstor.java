package com.example.aibodysizemeasurement.transtor;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


import static com.example.aibodysizemeasurement.activity.SegActivity.SHOW_THRESHOLD;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class MeasureTranstor {
    private int realheight;
    private Mat src;
    private Mat srcs;
    private double chest;
    private double frontbrw;
    private double frontbrt;
    private double frontbuw;
    private double frontbut;
    private double butto;
    private double waist;
    private double crotch;
    private double shoulder;
    private Handler mMyHandler;
    private double footSt,footFt;
    int width,height;
    double[][] cont={} ;//正面轮廓
    double[][] cont1={} ;//侧面轮廓
    private Bitmap bitF,bitS;
    private Bitmap bitmap;
    private double armw;

    public double getArmw() {
        return armw;
    }

    public double getFrontchest() {
        return frontchest;
    }

    public double getWatohip() {
        return watohip;
    }

    private double frontchest;
    private double watohip;

    public MeasureTranstor(int realheight, Mat src, Mat srcs,double footSt,double footFt,Handler mMyHandler){
       this.realheight=realheight;
       this.src=src;
       this.srcs=srcs;
       this.mMyHandler=mMyHandler;
       this.footFt=footFt;
       this.footSt=footSt;

   }

   public Bitmap getBitF(){
        return bitF;
   }

    public Bitmap getBitS(){
        return bitS;
    }

    public double getChest(){
        return chest;
    }
    public double getButto() {
        return butto;
    }
    public double getShoulder() {
        return shoulder;
    }

    public double getWaist() {
        return waist;
    }

    public double getCrotch() {
        return crotch;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Bitmap MeasureBody() {
        //从可变参数的数组中拿到第0位的图片地址

        width=src.width();
        height=src.height();
        //灰度化
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(srcs, srcs, Imgproc.COLOR_BGR2GRAY);
        //二值化
        Imgproc.threshold(src, src, 100, 255, THRESH_BINARY);
        Imgproc.threshold(srcs, srcs, 100, 255, THRESH_BINARY);



        //查找轮廓
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));
        //侧面轮廓
        List<MatOfPoint> Scontours = new ArrayList<MatOfPoint>();
        Mat Shierarchy = new Mat();
        Imgproc.findContours(srcs, Scontours, Shierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,
                new Point(0, 0));



        double maxl = width;
        double maxr = 0;
        double maxh = height;
        double lmaxf = 0;
        double rmaxf = 0;
        double ly=0,hx=0,lfy=0,lfx=0,ry=0,rfx=0;
        int lj=0,rj=0,hj=0,lfj=0,rfj=0;
        int contnum=0,scontnum=0;
        for (MatOfPoint point : contours) {
            MatOfPoint2f newPoint = new MatOfPoint2f(point.toArray());
            Log.i("aaron", "MatOfPoint2f " + newPoint.total());
            double[] temp;
            Log.i("aaron", "Point----------------- ");
            if (newPoint.total() < 500) {
                continue;
            }
            contnum=(int)newPoint.total();
            cont = new double[contnum][2];
            //遍历轮廓点
            for (int j = 0; j < contnum; j++) {
                temp = newPoint.get(j, 0);
                cont[j][0] = temp[0];
                cont[j][1] = temp[1];

                if (cont[j][0] < maxl) {
                    maxl = cont[j][0];
                    ly = cont[j][1];
                    lj = j;
                }
                if (cont[j][0] > maxr) {
                    maxr = cont[j][0];
                    ry = cont[j][1];
                    rj = j;
                }
                if (cont[j][1] < maxh) {
                    maxh = cont[j][1];
                    hx = cont[j][0];
                    hj = j;
                }
                //左脚
                if (cont[j][1] > lmaxf && cont[j][0] < width / 2) {
                    lmaxf = cont[j][1];
                    lfx = cont[j][0];
                    lfj = j;
                }
                //右脚
                if (cont[j][1] > rmaxf && cont[j][0] > width / 2) {
                    rmaxf = cont[j][1];
                    rfx = cont[j][0];
                    rfj = j;
                }

            }
        }
        //胯点
        double crotchh=height;
        double crotchx=0;
        double crotchy=0;

        for (int i=lfj; i<=rfj;i++) {
            if(cont[i][1] < crotchh) {
                crotchh = cont[i][1];
                crotchx = cont[i][0];
                crotchy = cont[i][1];
            }
        }
        // 找左腋点
        double laxily = ly;
        double laxilx=0;

        for( int i=lj; i<=lfj;i++) {
            if (cont[i][1] < laxily) {
                laxily = cont[i][1];
                laxilx = cont[i][0];
            }
        }
        //找右腋点
        double raxily = ry;
        double raxilx=0;

        for(int i=rfj; i<=rj;i++) {
            if (cont[i][1] < raxily){
                raxily = cont[i][1];
                raxilx = cont[i][0];
            }
        }
        //找出臂围外点
        double lk=(ly-laxily+30)/(laxilx-maxl-10);
        double rk=(ry-raxily+30)/(maxr-raxilx-10);
        Point point1=arm(width,laxilx,laxily,'L',1/lk);//loutarmx,loutarmy
        Point point2=arm(width, raxilx, raxily, 'R', 1/rk);//routarmx, routarmy
        double a1=sqrt((point1.x-laxilx)*(point1.x-laxilx)+(point1.y-laxily)*(point1.y-laxily));
        double a2=sqrt((point2.x-raxilx)*(point2.x-raxilx)+(point2.y-raxily)*(point2.y-raxily));
        //臂围
        armw =  realheight / (footFt - maxh) *((a1+a2)/2*3.1415926*0.8);

        //侧面轮廓
        double maxhs = height;
        double maxfs = 0;
        double hsx,fsx;
        int hsj=0,fsj;
        for (MatOfPoint point : Scontours) {
            MatOfPoint2f newPoint = new MatOfPoint2f(point.toArray());
            double[] temp;
            if (newPoint.total() < 500) {
                continue;
            }
            scontnum = (int) newPoint.total();
            cont1 = new double[scontnum][2];
            //遍历轮廓点
            for (int j = 0; j < scontnum; j++) {
                temp = newPoint.get(j, 0);
                cont1[j][0] = temp[0];
                cont1[j][1] = temp[1];

                if (cont1[j][1] < maxhs) {
                    maxhs = cont1[j][1];
                    hsx = cont1[j][0];
                    hsj = j;
                }

                if (cont1[j][1] > maxfs) {
                    maxfs = cont1[j][1];
                    fsx = cont1[j][0];
                    fsj = j;
                }
            }

        }

//肩宽
        double lshoulderx = laxilx;
        double rshoulderx = raxilx;
        double lshouldery=0,rshouldery=0;



        //找左肩点
        int m = lj;
        double min=9999;
        while (cont[m][1] > maxh) {
            if (abs(cont[m][0] - laxilx) < 7) {
                break;
            }
            m -= 1;
        }

        for(int i=m-10;i<m+10;i++){
            if(sqrt(cont[i][0]*cont[i][0]+(cont[i][1]-10)*(cont[i][1]-10))<min){
                min=sqrt(cont[i][0]*cont[i][0]+(cont[i][1]-10)*(cont[i][1]-10));
                lshouldery = cont[i][1];
                lshoulderx = cont[i][0];
            }
        }


        // 找右肩点
        min=9999;
        m = rj;
        while (cont[m][1] >maxh) {
            if (abs(cont[m][0] - raxilx) < 7){
                break;
            }
            m += 1;
        }

        for(int i=m-10;i<m+10;i++){
            if(sqrt((width-cont[i][0])*(width-cont[i][0])+(cont[i][1]-10)*(cont[i][1]-10))<min){
                min=sqrt((width-cont[i][0])*(width-cont[i][0])+(cont[i][1]-10)*(cont[i][1]-10));
                rshouldery = cont[i][1];
                rshoulderx = cont[i][0];
            }
        }

        double shoulderw = realheight / (footFt - maxh) * (rshoulderx - lshoulderx);
        double sideshouldery = ((rshouldery + lshouldery) / 2 - maxh) / (footFt - maxh) * (footSt - maxhs) + maxhs;
        //肩厚
        double shouldert=0;
        for( int j=0 ;j<width - 1;j++) {
            if (srcs.get((int)sideshouldery,j)[0]==255.0){
                shouldert = shouldert + 1;
            }
        }
        shouldert=shouldert*realheight / (footSt - maxhs)*0.5*0.866;

        shoulder=0.398*shoulderw+0.019*shoulderw*shouldert+0.197*shouldert*shouldert-2.945*shouldert+34.221;

        //将正面得到的脖子高度投影到侧面方便划定胸部范围
        double footF=(footFt-maxh)*0.01+footFt;
        double footS=(footSt-maxhs)*0.03+footSt;
        double sideneck = ((lshouldery+rshouldery)/2-maxh)/(footF-maxh)*(footS-maxhs)*0.92+maxhs;
        double other = 0.32*(footS-maxhs)+maxhs;
        int r1=0;
        int r2=0,x1=0,x2=0;
        //分别找到胸部上下边界与轮廓的交点
        for (int k=0; k<width-1;k++) {

            if (srcs.get((int)sideneck,k)[0]!=srcs.get((int)sideneck,k + 1)[0]){
                r1 = r1 + 1;
            }
            if (r1 == 2){
                x1 = k;
                break;
            }
        }
        for (int k=0; k<width-1;k++) {

            if (srcs.get((int)other,k)[0]!=srcs.get((int)other,k + 1)[0]){
                r2 = r2 + 1;
            }
            if (r2 == 2){
                x2 = k;
                break;
            }
        }
        int x3=2*x2-x1;
        int x4=x2;


        //计算倾斜角度'''
        double withRect = Math.sqrt((x2 - x1)*(x2 - x1) + (other - sideneck)*(other - sideneck));
        double angle = Math.acos((x2 - x1) / withRect) * (180 / 3.1415926);
        double y3=other-(x2-x1)*abs(1/Math.tan(Math.toRadians(angle)));
        double y4=sideneck-(x2-x1)*abs(1/ Math.tan(Math.toRadians(angle)));
        Mat imgOut=rotate(new Point(x1,sideneck),new Point(x4,y4),new Point(x3,y3),new Point(x2,other),angle);

        bitmap=Bitmap.createBitmap(imgOut.width(), imgOut.height(),  Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgOut,bitmap,true);

        int count1=0;
        int max1=0;
        //找到胸部图中的最高点'''
        int width2=bitmap.getWidth();
        int high2=bitmap.getHeight();
        int mb=0;
        for (int i=0; i<width2;i++) {
            for (int j=0;j<high2;j++) {
                if (imgOut.get(j,i)[0]==255.0){
                    count1 = count1 + 1;
                }
            }
            if (count1 > max1){
                max1 = count1;
                mb = i;
            }
            count1 = 0;
        }
        //计算出胸高点在原图中的实际位置'''
        double br=sideneck+mb*abs(sin(toRadians(angle)));

        //胸厚
        int brt=0;
        for( int j=0 ;j<width - 1;j++) {
            if (srcs.get((int)br,j)[0]==255.0){
                brt = brt + 1;
            }
        }

        double frontbr=(br-maxhs)/(footS-maxhs)*(footF-maxh)+maxh;
        //找胸宽'''
        int max ;
        int boundbw = 0;
        ArrayList<Integer> bw = new ArrayList<>();
        ArrayList<Integer> wb = new ArrayList<>();

        for (int j =0;j<width - 1;j++){
            if (src.get((int)frontbr,j)[0]==0.0&& src.get((int)frontbr,j+1)[0]==255.0){
                bw.add(j);
                boundbw = boundbw + 1;
            }
            if( src.get((int)frontbr,j)[0] ==255.0 && src.get((int)frontbr,j+1)[0] ==0.0){
                wb.add(j);
            }
        }
        if (boundbw == 3 ) {
            max = wb.get(1) - bw.get(1);
        }
        else {
            max = (int)(raxilx - laxilx);
        }

        //计算胸围
        frontbrw=max*realheight/(footF-maxh);
        frontbrt=brt*realheight/(footS-maxhs);

        chest = 0.818 * frontbrw + 1.855 * frontbrt + 0.087 * frontbrw * frontbrw + 0.115 * frontbrt * frontbrt - 0.193 * frontbrt * frontbrw +11.454;

        //前胸宽
        frontchest=0.5*chest;

        //臀围
        //臀高点
        Point bu = findbu(hsj, (int)((footS - maxhs) * 0.43+maxhs), (int)((footS - maxhs) * 0.61+maxhs));

        double frontbu=(bu.y-maxhs)/(footS-maxhs)*(footF-maxh)*1.05+maxh;

        //找臀厚'''
        int but=0;
        for (int j=(int)bu.x; j<width-1;j++) {
            if (srcs.get((int)bu.y, j)[0]==255.0){
                but = but + 1;
            }
        }
        //找臀宽'''
        double buw = 0;

        int ti1,ti2;
        for(ti1=lfj;cont[ti1][1]>bu.y;ti1--);
        for(ti2=rfj;cont[ti2][1]>bu.y;ti2++);
        buw=cont[ti2][0]-cont[ti1][0];


        //计算臀围
        frontbuw = buw * (realheight/ (footF - maxh));
        frontbut = but *( realheight / (footS - maxhs));
        butto=-1.522*frontbuw+0.039*frontbuw*frontbuw+0.041*frontbut*frontbuw+65.589;

        //大腿根围
        int lcrox=(int)crotchx;
        int rcrox=(int)crotchx;
        int lleg=0;
        int rleg=0;
        while (src.get((int)crotchy,lcrox-1)[0]!=0.0) {
            lleg = lleg + 1;
            lcrox = lcrox - 1;
        }

        while (src.get((int)crotchy,rcrox + 1)[0] != 0.0){
            rleg = rleg + 1;
            rcrox = rcrox + 1;
        }

        double crosidew=((lleg+rleg)/2)*realheight/(footF - maxh);
        double crosidey=(crotchy-maxh)/(footF-maxh)*(footS-maxhs)+maxhs;

        int croside=0;
        for (int i=0; i<width-1;i++) {
            if (srcs.get((int)crosidey,i)[0]==255.0){
                croside = croside + 1;
            }
        }

        //侧面胯
        double crosidet=croside*realheight/(footF - maxh);

        crotch=-0.1*crosidew*crosidew+0.09*crosidew*crosidet+0.002*crosidet*crosidet-1.777*crosidet+4.964*crosidew-0.525;

        //腰围
        double mid =( (footF - maxh)*0.4 )+ maxh;
        bw.clear();
        wb .clear();
        boundbw = 0;
        min = 9999;
        int maxin = 0;
        max=0;
        int yy = 0,yy1=0;
        //正面腰宽
        for( int i=(int)(laxily<=raxily?raxily:laxily)+1;i< frontbu;i++){
            for (int j=0; j<width - 1;j++) {
                if( src.get(i, j)[0] ==0.0 && src.get(i, j + 1)[0 ] ==255.0){
                    bw.add(j);
                    boundbw = boundbw + 1;
                }
                if (src.get(i, j)[0] ==255.0 && src.get( i, j + 1)[0] ==0){
                    wb.add(j);
                }
            }
            if (boundbw == 1){
                if (min > (wb.get(0) - bw.get(0))){
                    min = wb.get(0) - bw.get(0);
                    yy = i;
                }
                if((wb.get(0) - bw.get(0))>max){
                    max=wb.get(0) - bw.get(0);
                    yy1=i;
                }
            }
            else if (boundbw >1){
                for (int g=0; g<boundbw;g++){
                    if ((wb.get(g) - bw.get(g)) >maxin ){
                        maxin = wb.get(g) - bw.get(g);
                    }
                }
                if (maxin<min){
                    min = maxin;
                    yy = i;
                }
                if(maxin>max){
                    max=maxin;
                    yy1=i;
                }
            }
            maxin = 0;
            boundbw = 0;
            bw.clear();
            wb.clear();
        }
        if((frontbu-yy)<50){
            yy=yy1;
            min=max;
        }
        //侧面腰厚
        double waistw = (realheight / (footF - maxh)) * min;
        double yys=(yy-maxh)/(footF+maxh)*(footS+maxhs)+maxhs;
        double waistt=0;
        for (int i=0; i<width-1;i++) {
            if (srcs.get((int)yys,i)[0]==255.0){
                waistt = waistt + 1;
            }
        }
        waistt=waistt*(realheight/(footS-maxhs));

        waist=0.32*waistw*waistw+0.042*waistw*waistt-0.010*waistt*waistt-16.765*waistw-0.78*waistt+288.044;


        //腰到臀的距离
        watohip=abs(bu.y-yys)*(realheight/(footS-maxhs));

//        Message msg=Message.obtain();
//        msg.what=TEXT;
//        Bundle bundle=new Bundle();
//        bundle.putString("test","(mid + (footF - maxh) * 0.15):"+(mid + (footF - maxh) * 0.15)+"yy:"+yy);
//        msg.setData(bundle);
//        mMyHandler.sendMessage(msg);



        Imgproc.cvtColor(src, src, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(srcs, srcs, Imgproc.COLOR_GRAY2BGR);


        Imgproc.circle(src,new Point(maxl,ly),6,new Scalar(0,0,255),3);//最左点
        Imgproc.circle(src,new Point(maxr,ry),6,new Scalar(0,0,255),3);//最右点
        Imgproc.circle(src,new Point(hx,maxh),6,new Scalar(0,0,255),3);//最上点
        Imgproc.circle(src,new Point(lfx,lmaxf),6,new Scalar(0,0,255),3);//左脚
        Imgproc.circle(src,new Point(rfx,rmaxf),6,new Scalar(0,0,255),3);//右脚
        Imgproc.circle(src,new Point(crotchx,crotchy),6,new Scalar(0,0,255),3);//胯点
        Imgproc.circle(src,new Point(laxilx,laxily),6,new Scalar(0,0,255),3);//左腋点
        Imgproc.circle(src,new Point(raxilx,raxily),6,new Scalar(0,0,255),3);//右腋点
        Imgproc.circle(src,point1,6,new Scalar(0,0,255),3);//左臂外点
        Imgproc.circle(src,point2,6,new Scalar(0,0,255),3);//右臂外点
        Imgproc.circle(srcs,new Point(x1,sideneck),6,new Scalar(0,0,255),3);//右臂外点
        Imgproc.circle(srcs,new Point(x4,y4),6,new Scalar(0,0,255),3);//右臂外点
        Imgproc.circle(srcs,new Point(x3,y3),6,new Scalar(0,0,255),3);//右臂外点
        Imgproc.circle(srcs,new Point(x2,other),6,new Scalar(0,0,255),3);//右臂外点
        //Imgproc.line(src,new Point(0,shoulderdn),new Point(width,shoulderdn),new Scalar(0,0,255),5);//肩下范围
        //Imgproc.line(src,new Point(0,neckup),new Point(width,neckup),new Scalar(0,0,255),5);//颈上范围
        Imgproc.line(src,new Point(0,footF),new Point(width,footF),new Scalar(0,0,255),5);//地面
        Imgproc.line(srcs,new Point(0,br),new Point(width,br),new Scalar(0,0,255),5);//胸高
        Imgproc.line(srcs,new Point(0,bu.y),new Point(width,bu.y),new Scalar(0,0,255),5);//侧面臀高
        Imgproc.line(srcs,new Point(0,(int)((footS - maxhs) * 0.43+maxhs)),new Point(width,(int)((footS - maxhs) * 0.43+maxhs)),new Scalar(0,255,255),5);//侧面臀高
        Imgproc.line(srcs,new Point(0,(int)((footS - maxhs) * 0.61+maxhs)),new Point(width,(int)((footS - maxhs) * 0.61+maxhs)),new Scalar(0,255,255),5);//侧面臀高
        Imgproc.line(src,new Point(0,frontbu),new Point(width,frontbu),new Scalar(0,0,255),5);//正面臀高
        Imgproc.line(src,new Point(0,yy),new Point(width,yy),new Scalar(0,255,255),5);//正面腰高
        Imgproc.line(srcs,new Point(0,footS),new Point(width,footS),new Scalar(0,0,255),5);//地面
        Imgproc.line(srcs,new Point(0,sideshouldery),new Point(width,sideshouldery),new Scalar(0,0,255),5);//sideshoulder
        Imgproc.circle(src,new Point(lshoulderx,lshouldery),6,new Scalar(0,0,255),3);//右肩点
        Imgproc.circle(src,new Point(rshoulderx,rshouldery),6,new Scalar(0,0,255),3);//右肩点

        bitF=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitS=bitF.copy(Bitmap.Config.ARGB_8888,true);
        Utils.matToBitmap(src, bitF);
        Utils.matToBitmap(srcs, bitS);
        mMyHandler.sendEmptyMessage(SHOW_THRESHOLD);
        return bitmap;
    }

    /**
     * 寻找臀高点
     */
    private Point findbu(int hsj, int a, int b) {

        while(cont1[hsj][1]<=a){
            hsj+=1;
        }
        double min=999;
        int j=0;
        int i;
        for(i=hsj;cont1[i][1]<b;i++){
            if(cont1[i][0]<min){
                min=cont1[i][0];
                j=i;
            }
        }
//        if((cont1[j][1]-a)<5||(b-cont1[j][1])<5){
//            return new Point(cont1[(hsj+i)/2][0],cont1[(hsj+i)/2][1]);
//        }
        return new Point(cont1[j][0],cont1[j][1]);
    }

    /**
     * 将胸部图像旋转切割便于找胸高点
     */
    private Mat rotate(Point point1, Point point2, Point point3, Point point4,double angle) {
//        double withRect = Math.sqrt((point4.x - point1.x)*(point4.x - point1.x) + (point4.y - point1.y)*(point4.y - point1.y));  // 矩形框的宽度
//        double heightRect = Math.sqrt((point1.x - point2.x) *(point1.x - point2.x) + (point1.y - point2.y) *(point1.y - point2.y) );
//        double angle = Math.acos((point4.x - point1.x) / withRect) * (180 / 3.1415926);  // 矩形框旋转角度
//        if(point4.y < point1.y) {
//            angle = -angle;
//        }

        Mat rotateMat = Imgproc.getRotationMatrix2D(new Point(width / 2, height / 2), angle, 1) ; // 按angle角度旋转图像
        int heightNew = (int)(width * abs(Math.sin(toRadians(angle))) + height * abs(Math.cos(toRadians(angle))));
        int widthNew = (int)(height * abs(Math.sin(toRadians(angle))) + width * abs(Math.cos(toRadians(angle))));

        rotateMat.put(0, 2, rotateMat.get(0, 2)[0] + (widthNew - width) / 2);
        rotateMat.put(1, 2, rotateMat.get(1, 2)[0] + (heightNew - height) / 2);

        Mat dst = new Mat(heightNew, widthNew, CvType.CV_8UC(1));
        Imgproc.warpAffine(srcs, dst,rotateMat,new Size(widthNew, heightNew),Imgproc.INTER_NEAREST);
        double p1x=point1.x*cos(toRadians(angle))+point1.y*sin(toRadians(angle));
        double p1y=point1.y*cos(toRadians(angle))- point1.x*sin(toRadians(angle))+width*sin(toRadians(angle));

        double p3x=point3.x*cos(toRadians(angle))+point3.y*sin(toRadians(angle));
        double p3y=point3.y*cos(toRadians(angle))- point3.x*sin(toRadians(angle))+width*sin(toRadians(angle));


        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2BGR);

//        Message msg=Message.obtain();
//        msg.what=TOAST_MSG;
//        Bundle bundle=new Bundle();
//        bundle.putDouble("angle",angle);
//        bundle.putDouble("x",p1x);
//        bundle.putDouble("y",p1y);
//        bundle.putDouble("x0",point1.x);
//        bundle.putDouble("y0",point1.y);
//        bundle.putDouble("w",dst.width());
//        bundle.putDouble("h",dst.height());
//        msg.setData(bundle);
//        mMyHandler.sendMessage(msg);


        //裁减得到的旋转矩形框

        Rect rect=new Rect(new Point(p1x,p1y),new Point(p3x,p3y));
        Mat imgOut = new Mat(dst,rect);


        return imgOut ; //roated image
    }

    /**
     * @param width 图片宽度
     * @param axilx 腋点x
     * @param axily 腋点y
     * @param L_R
     * @param k 斜率
     * @return 手臂外点
     */
    private Point arm(int width, double axilx, double axily, char L_R, double k) {
        if (L_R == 'L') {
            while (axilx > 0) {
                axily = axily - 2*k;
                axilx = axilx - 2;

                if (src.get((int)axily,(int)axilx)[0]==0.0){
                    break;
                }
            }

            return new Point(axilx,axily);
        }
        else {
            while (axilx <width) {
                axily = axily - 2*k;
                axilx = axilx + 2;

                if (src.get((int)axily,(int)axilx)[0]==0.0){
                    break;
                }
            }
            return new Point(axilx,axily);
        }
    }


}
