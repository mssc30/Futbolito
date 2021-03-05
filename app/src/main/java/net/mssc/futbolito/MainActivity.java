package net.mssc.futbolito;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor acelerometro;
    private int jug1, jug2;

    //OBJETO DE MI CLASE VISTA
    private MiVista miVista = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //INICIALIZAR OBJETOS NECESARIOS, COMO EL SENSOR MANAGER Y EL ACELEROMETRO
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        jug1=0;
        jug2=0;

        miVista = new MiVista(this);

        //DETERMINAR EL CONTENT A UNA VIEW, NO A UN LAYOUT
        setContentView(miVista);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //INTERVALO EN QUE LOS EVENTOS DEL SENSOR SE ENVIAN A LA APP A TRAVES DE onSensorChanged()
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //DESACTIVAR LOS SENSORES QUE NO SE NECESITAN
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //SE EJECUTA CUANDO HAY UN VALOR NUEVO DEL SENSOR

        //VERIFICAR SI EL CAMBIO VIENE DEL ACELEROMETRO
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //LLAMAR AL METODO EN LA CLASE
            miVista.eventoSensor(event);
        }
    }

    public class MiVista extends View {
        //TAMAÑO PELOTA
        private static final int RADIO_CIRCULO = 45;

        //VARIABLES NECESARIAS
        private Paint miPaint, miPaint2;
        private int x = 360; //VALOR DE INICIO
        private int y = 685; //CENTRO PANTALL
        private int ancho;
        private int alto;

        public MiVista(Context context) {
            super(context);

            //INICIALIZAR OBJETO PAINT PARA PELOTA
            miPaint = new Paint();
            miPaint.setColor(Color.MAGENTA);

            //INICIALIZAR OBJETO PAINT PARA MARCADOR
            miPaint2 = new Paint();
            miPaint2.setColor(Color.BLACK);
            miPaint2.setTextSize(16);
        }

        //METODO QUE SE LLAMA CUANDO ANDROID CALCULA EL TAMAÑO DE LA PANTALLA
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            ancho = w;
            alto = h;
        }

        //EVENTO QUE SE EJECUTA CADA QUE HAY NUEVOS DATOS EN EL SENSOR
        public void eventoSensor (SensorEvent event) {
            //CALCULAR EL VALOR NUEVO DE X, Y
            x = x - (int) event.values[0];
            y = y + (int) event.values[1];

            //ASEGURA QUE NO SE DIBUJA FUERA DE LA VISTA.
            //SI SE SALE DE LA VISTA, SOLO DIBUJA LOS BORDES + TAMAÑO CIRCULO

            if (x <= 0 + RADIO_CIRCULO) {
                x = 0 + RADIO_CIRCULO;
            }

            if (x >= ancho - RADIO_CIRCULO) {
                x = ancho - RADIO_CIRCULO;
            }

            if (y <= 0 + RADIO_CIRCULO) {
                y = 0 + RADIO_CIRCULO;
            }

            if (y >= alto - RADIO_CIRCULO) {
                y = alto - RADIO_CIRCULO;
            }

            //SI LA PELOTA CAYO EN LA PORTERIA 1
            if(((x > 218 & x < 495) & (y > 44 & y < 186))){
                jug1++;
                x = 360; //VALOR DE INICIO
                y = 685; //CENTRO PANTALL
            }

            //SI LA PORTERIA CAYO EN LA PORTERIA 2
            if(((x > 217 & x < 495) & (y > 1173 & y < 1324))){
                jug2++;
                x = 360; //VALOR DE INICIO
                y = 685; //CENTRO PANTALL
            }
        }

        //METODO QUE HACE QUE SE PINTE EL CANVAS
        @Override
        protected void onDraw(Canvas canvas) {
            //PINTAR FONDO CANCHA
            Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.fondo_fut);
            Rect screen = new Rect(0, 0, ancho, alto);
            canvas.drawBitmap(background, null, screen, null);

            //PINTAR MARCADOR
            float textSize = miPaint2.getTextSize();
            miPaint2.setTextSize(textSize * 10);
            canvas.drawText(jug1+"", 320, 185, miPaint2);
            canvas.drawText(jug2+"", 320, 1323, miPaint2);
            miPaint2.setTextSize(textSize);

            //PINTAR PELOTA
            canvas.drawCircle(x, y, RADIO_CIRCULO, miPaint);

            //HACE QUE SE ESTE DIBUJANDO CONSTANTEMENTE
            invalidate();
        }
    }
}