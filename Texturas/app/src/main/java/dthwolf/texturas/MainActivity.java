package dthwolf.texturas;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.Arrays;

import static org.opencv.core.Core.FONT_HERSHEY_PLAIN;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {

    private CameraBridgeViewBase mOpenCvCameraView;

    private Mat mRgba;

    private Mat mMascara;
    private Mat mHsv;


    private int mContenedorH;
    private int mContenedorS;
    private int mContenedorV;
    private int divAncho;
    private int divAlto;

    private MatOfInt mContenedores;
    private MatOfFloat mRangos;
    private MatOfInt mCanales;

    Rect a_rectangulo1;
    Rect a_rectangulo2;
    Rect a_rectangulo3;

    private boolean a_tomaTextura1 = true;
    private boolean a_capturaTextura1 = false;
    private boolean a_tomaTextura2 = false;
    private boolean a_capturaTextura2 = false;
    private boolean a_tomaTextura3 = false;
    private boolean a_capturaTextura3 = false;

    private Mat mTextura;
    private Mat mHistograma;
    private Mat mTextura2;
    private Mat mHistograma2;
    private Mat mTextura3;
    private Mat mHistograma3;

    private boolean banRectangulo2 = false;
    private boolean banRectangulo3 = false;
    double a_ang=0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(480, 320);
        //mOpenCvCameraView.enableFpsMeter();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
    }

    @Override
    public void onPause()
    {
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        divAncho = width/16;
        divAlto = height/16;
        mRgba = new Mat(height, width, CvType.CV_8UC3);
        mTextura = new Mat(height, width, CvType.CV_8UC3);
        mHsv = new Mat(height, width, CvType.CV_8UC3);
        mMascara = new Mat(height, width, CvType.CV_8UC1);
        Imgproc.rectangle(mMascara,new Point(divAncho*7, divAlto*7), new Point(divAncho*9, divAlto*9), new Scalar(255, 255, 255), Core.FILLED);
        mContenedorH = 50;
        mContenedorS = 50;
        mContenedorV = 20;
        mContenedores = new MatOfInt (mContenedorH, mContenedorS, mContenedorV);
        mRangos = new MatOfFloat(0, 179, 0, 255, 0, 255);
        mCanales = new MatOfInt(0, 1, 2);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mTextura.release();
        mMascara.release();
        mHsv.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (a_tomaTextura1)
        {
            a_rectangulo1 = new Rect(new Point(divAncho*7, divAlto*7), new Point(divAncho*9, divAlto*9));
            Imgproc.rectangle(mRgba, a_rectangulo1.tl(), a_rectangulo1.br(), new Scalar(255,0, 0));
            if(a_capturaTextura1)
            {
                a_tomaTextura1 = false;
                a_capturaTextura1 =  false;
                a_tomaTextura2 = true;
                mTextura = (Mat) inputFrame.rgba().clone();
                Imgproc.cvtColor(mTextura, mTextura, Imgproc.COLOR_RGBA2RGB);
                Imgproc.cvtColor(mTextura, mTextura, Imgproc.COLOR_RGB2HSV_FULL); //Cambio espacio de trabajo a HSV.
                mHistograma = new Mat();
                Imgproc.calcHist(Arrays.asList(mTextura), mCanales, mMascara, mHistograma, mContenedores, mRangos, false);
            }
            return mRgba;
        }

        if (a_tomaTextura2)
        {
            a_rectangulo2 = new Rect(new Point(divAncho*7, divAlto*7), new Point(divAncho*9, divAlto*9));
            Imgproc.rectangle(mRgba, a_rectangulo2.tl(), a_rectangulo2.br(), new Scalar(0,255,0));
            if(a_capturaTextura2)
            {
                a_tomaTextura3 = true;
                a_tomaTextura2 = false;
                a_capturaTextura2 =  false;
                banRectangulo2 = true;
                mTextura2 = (Mat) inputFrame.rgba().clone();
                Imgproc.cvtColor(mTextura2, mTextura2, Imgproc.COLOR_RGBA2RGB);
                Imgproc.cvtColor(mTextura2, mTextura2, Imgproc.COLOR_RGB2HSV_FULL); //Cambio espacio de trabajo a HSV.
                mHistograma2 = new Mat();
                Imgproc.calcHist(Arrays.asList(mTextura2), mCanales, mMascara, mHistograma2, mContenedores, mRangos, false);
            }
        }

        if (a_tomaTextura3)
        {
            a_rectangulo3 = new Rect(new Point(divAncho*7, divAlto*7), new Point(divAncho*9, divAlto*9));
            Imgproc.rectangle(mRgba, a_rectangulo3.tl(), a_rectangulo3.br(), new Scalar(0,0,255));
            if(a_capturaTextura3)
            {
                a_tomaTextura3 = false;
                a_capturaTextura3 =  false;
                banRectangulo3 = true;
                mTextura3 = (Mat) inputFrame.rgba().clone();
                Imgproc.cvtColor(mTextura3, mTextura3, Imgproc.COLOR_RGBA2RGB);
                Imgproc.cvtColor(mTextura3, mTextura3, Imgproc.COLOR_RGB2HSV_FULL); //Cambio espacio de trabajo a HSV.
                mHistograma3 = new Mat();
                Imgproc.calcHist(Arrays.asList(mTextura3), mCanales, mMascara, mHistograma3, mContenedores, mRangos, false);
            }
        }

        Imgproc.cvtColor(mRgba, mHsv, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(mHsv, mHsv, Imgproc.COLOR_RGB2HSV_FULL); //Cambio espacio de trabajo a HSV.

        Mat mBackproject = new Mat();
        Imgproc.calcBackProject(Arrays.asList(mHsv), mCanales, mHistograma, mBackproject, mRangos, 1);

        Video.CamShift(mBackproject, a_rectangulo1, new TermCriteria(TermCriteria.EPS | TermCriteria.COUNT, 20, 1));
        Imgproc.rectangle(mRgba, a_rectangulo1.tl(), a_rectangulo1.br(), new Scalar(255,0, 0),2);

        if(banRectangulo2){
            Mat mBackproject2 = new Mat();
            Imgproc.calcBackProject(Arrays.asList(mHsv), mCanales, mHistograma2, mBackproject2, mRangos, 1);

            Video.CamShift(mBackproject2, a_rectangulo2, new TermCriteria(TermCriteria.EPS | TermCriteria.COUNT, 20, 1));
            Imgproc.rectangle(mRgba, a_rectangulo2.tl(), a_rectangulo2.br(), new Scalar(0,255, 0),2);
        }

        if(banRectangulo3){
            Mat mBackproject3 = new Mat();
            Imgproc.calcBackProject(Arrays.asList(mHsv), mCanales, mHistograma3, mBackproject3, mRangos, 1);

            Video.CamShift(mBackproject3, a_rectangulo3, new TermCriteria(TermCriteria.EPS | TermCriteria.COUNT, 20, 1));
            Imgproc.rectangle(mRgba, a_rectangulo3.tl(), a_rectangulo3.br(), new Scalar(0,0, 255),2);

            //Primer Punto
            double v_x= a_rectangulo1.br().x - ((a_rectangulo1.br().x-a_rectangulo1.tl().x)/2);
            double v_y= a_rectangulo1.br().y - ((a_rectangulo1.br().y-a_rectangulo1.tl().y)/2);
            Point v_p1= new Point(v_x,v_y);

            v_x= a_rectangulo2.br().x - ((a_rectangulo2.br().x-a_rectangulo2.tl().x)/2);
            v_y= a_rectangulo2.br().y - ((a_rectangulo2.br().y-a_rectangulo2.tl().y)/2);
            Point v_p2= new Point(v_x,v_y);

            v_x= a_rectangulo3.br().x - ((a_rectangulo3.br().x-a_rectangulo3.tl().x)/2);
            v_y= a_rectangulo3.br().y - ((a_rectangulo3.br().y-a_rectangulo3.tl().y)/2);
            Point v_p3= new Point(v_x,v_y);

            Imgproc.line(mRgba,v_p1,v_p2, new Scalar(255,255,255), 2);
            Imgproc.line(mRgba,v_p2,v_p3, new Scalar(255,255,255), 2);

            double v_m1= (v_p2.y-v_p1.y)/(v_p2.x-v_p1.x);
            double v_m2= (v_p3.y-v_p2.y)/(v_p3.x-v_p2.x);
            double v_beta = Math.toDegrees(Math.atan((v_m2-v_m1)/(1+v_m1*v_m2)));

            if(v_p3.x>v_p1.x&&v_p3.y<v_p2.y){
                v_beta=v_beta*-1;
                v_beta=180+v_beta;
            }
            if(v_p3.x<v_p1.x&&v_p3.y<v_p2.y){
                v_beta=180+v_beta;
            }
            if(v_p3.x>v_p1.x&&v_p3.y>v_p2.y){
                v_beta=v_beta*-1;
            }

            String v_angulo ="Grados: "+v_beta;
            Imgproc.putText(mRgba,v_angulo,new Point(0,15),FONT_HERSHEY_PLAIN,1,new Scalar(255,255,0));

        }
        return mRgba;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(a_tomaTextura1){
            a_capturaTextura1 = true;
        }
        if(a_tomaTextura2){
            a_capturaTextura2 = true;
        }
        if(a_tomaTextura3){
            a_capturaTextura3 = true;
        }
        if(!a_tomaTextura1&&!a_tomaTextura2&&!a_tomaTextura3){
            a_tomaTextura1 = true;
            banRectangulo2 = false;
            banRectangulo3 = false;
        }
        return false;
    }
}
