package com.example.root.logsreader;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {
    private StringBuilder log;
    private Button RM,RB,SN,LM,Clear,Listen,Save;
    private TextView tv;
    private String tagFilter="(D/BUENO|D/MALO|E/)";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("HOLA", "Inicio del proyecto");
        inicializar();
        leer_logs();
        buttonsActions();
    }

    public void inicializar(){
        RB= (Button) findViewById(R.id.btnRB);
        RM= (Button) findViewById(R.id.btnRM);
        LM= (Button) findViewById(R.id.btnLM);
        SN= (Button) findViewById(R.id.btnSN);
        Clear= (Button) findViewById(R.id.btnClearLogs);
        Listen= (Button) findViewById(R.id.btnListenLogs);
        tv = (TextView)findViewById(R.id.txtLogs);
    }

    public void buttonsActions(){
        RB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUENO", "Rene Bueno");
            }
        });



        RM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MALO", "Rene Malo");
            }
        });

        SN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NEUTRO", "Sergio Neutro");
            }
        });

        LM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MALO 2", "Lily Malo");
            }
        });
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OPERATION", "Borrando Logs");
                try {
                    limpiar_logs(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OPERATION", "Escuchando Logs");
                leer_logs();
            }
        });
    }

    public void leer_logs(){
        Process mLogcatProc = null;
        BufferedReader reader = null;
        try {
            Process process = Runtime.getRuntime().exec("logcat -d -v time");
            /*
            * -d = Buscar los logs y parar la busqueda
            *-v time para capturar la fecha y hora del log
            * http://adayofrequiem.blogspot.mx/2012/03/catch-logcat-programmatically-or-export.html
            * */
            reader= new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern pattern = Pattern.compile(tagFilter, 0); /*solo acepta los Logs que contengan
            las palabras Bueno o malo  del tipo d,
             tambien acepta los errores

            Pattern pattern = Pattern.compile("D/HOLA", 0); si tiene la palabra D/HOLA

            Referencia de Regular Expresions http://developer.android.com/reference/java/util/regex/Pattern.html
            */
            final StringBuilder log = new StringBuilder();
            String separator = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null)
            {
                if (pattern != null
                        && !pattern.matcher(line).find()) {
                    continue;
                }
                //anexar que inicie con la fecha del dia de hoy
                /* System.out.println(Str.startsWith("Welcome") );*/

                log.append(line);
                log.append(separator);
            }
            tv.setText(log.toString()); ///asignando a un textView
            guardarLogArchivoAndBorrar(log);
        } catch (IOException e) {

        }
    }

    public void limpiar_logs(int tipo) throws IOException {
        Process process = new ProcessBuilder()
                .command("logcat", "-c")
                .redirectErrorStream(true)
                .start();
        if(tipo==1) {
            tv.setText("Vaciado");
        }
    }

    public void guardarLogArchivo(StringBuilder log){
        /*Aqui lo que hace es sobreescribir*/
        //convert log to string
        final String logString = new String(log.toString());

        //create text file in SDCard
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/myLogcat");
        dir.mkdirs();
        File file = new File(dir, "logcat.txt");

        try {
            //to write logcat in text file
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(logString);
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void guardarLogArchivoAndBorrar(StringBuilder log) throws IOException {
        /*Aqui se anexa , no sobreescribe*/

        //convert log to string
        final String logString = new String(log.toString());

        //create text file in SDCard
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/myLogcat");
        dir.mkdirs();
        File logFile = new File(dir+"/logcat.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(logString);
            buf.newLine();
            buf.close();
            limpiar_logs(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // File file = new File(dir, "logcat.txt");
/*
        try {
            //to write logcat in text file
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.append(logString);
            osw.flush();
            osw.close();

            limpiar_logs(0);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
