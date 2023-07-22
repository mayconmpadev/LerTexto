package com.example.lertexto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends Activity {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageView micButton;
    private int currentApiVersion;
    int capitulo = 1;
    int versiculo = 1;
    String livro = "LIVRO NÃO ENCONTRADO";
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    TextView textLivro, textVersiculo;
    ArrayList<String> arrayList = new ArrayList<>();

    boolean inicio = false;
    boolean fim = true;
    String[] livros = {"Gênesis", "Êxodo", "Levítico", "Números", "Deuteronômio", "Josué", "Juízes", "Rute", "I Samuel", "II Samuel", "I Reis", "II Reis", "I Crônicas", "II Crônicas",
            "Esdras", "Neemias", "Ester", "Jó", "Salmos", "Provérbios", "Eclesiastes", "Cantares", "Isaías", "Jeremias", "Lamentações", "Ezequiel", "Daniel", "Oséias", "Joel", "Amós", "Obadias",
            "Jonas", "Miquéias", "Naum", "Habacuque", "Sofonias", "Ageu", "Zacarias", "Malaquias", "Mateus", "Marcos", "Lucas", "João", "Atos", "Romanos", "I Coríntios", "II Coríntios",
            "Gálatas", "Efésios", "Filipenses", "Colossenses", "I Tessalonicenses", "II Tessalonicenses", "I Timóteo", "II Timóteo", "Tito", "Filemom", "I Pedro", "II Pedro", "I João", "II João", "III João",
            "Hebreus", "Tiago", "Judas", "Apocalipse"};

    ArrayList<String> arrayLivros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


        setContentView(R.layout.activity_main);

        textLivro = findViewById(R.id.textLivro);
        textVersiculo = findViewById(R.id.textVersiculo);
        micButton = findViewById(R.id.imageMic);
        arrayLivros.clear();
        for (int i = 0; i < livros.length; i++) {
            arrayLivros.add(livros[i].toUpperCase());

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        editText = findViewById(R.id.text);
        // micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                textVersiculo.setText("");
                textLivro.setText("");
                micButton.setVisibility(View.VISIBLE);
                micButton.setBackgroundColor(Color.RED);

            }

            @Override
            public void onBeginningOfSpeech() {
                textVersiculo.setText("");
                //  textLivro.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                textLivro.setText("");
                textVersiculo.setText("");
                micButton.setBackgroundColor(Color.BLACK);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResults(Bundle bundle) {
                // micButton.setImageResource(R.drawable.ic_mic_black_off);
                micButton.setVisibility(View.INVISIBLE);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                micButton.setVisibility(View.INVISIBLE);
                micButton.setBackgroundColor(Color.RED);
                formatar(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    private void readFromFile(String sLivro, int capitulo, int versiculo) {


        InputStream is = this.getResources().openRawResource(R.raw.biblia);
        Scanner sc = null;
        try {
            StringBuffer result = new StringBuffer();
            sc = new Scanner(is);     // java.util.Scanner
            String line;
            while (sc.hasNextLine() & fim) {
                line = sc.nextLine();
                if (line.equals(" " + sLivro + " " + capitulo)) {
                    inicio = true;
                }
                if (inicio) {


                    if (line.equals(" " + sLivro + " " + (capitulo + 1))) {
                        fim = false;
                        break;
                    }


                    // result.append(line + "\n");
                    // if (line.substring(0,1).matches("[0-9]*")){
                    arrayList.add(line); // process the line
                    result.append(line + "\n");

                }
            }
           // Toast.makeText(getApplicationContext(), String.valueOf(arrayList.size()), Toast.LENGTH_SHORT).show();
            textLivro.setText(sLivro + " " + capitulo + ":" + versiculo);
            textVersiculo.setText(arrayList.get(versiculo).replaceAll("[0-9]", ""));
        } catch (Exception e) {
            e.printStackTrace();
           // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (sc != null) sc.close();
        }

    }


    private void formatar(String string) {
        String dita = string;
        capitulo = 1;
        versiculo = 1;
        livro = "LIVRO NÃO ENCONTRADO";
        fim = true;
        inicio = false;
        ArrayList<Integer> digitos = new ArrayList<>();
        arrayList.clear();
        digitos.clear();


        string = string.toUpperCase();
        string = string.replace("PRIMEIRO", "I");
        string = string.replace("PRIMEIRA", "I");
        string = string.replace("SEGUNDO", "II");
        string = string.replace("SEGUNDA", "II");
        string = string.replace("TERCEIRO", "III");
        string = string.replace("TERCEIRA", "III");
        string = string.replace("CAPÍTULO", "\u00A0");
        string = string.replace("VERSÍCULO", "\u00A0");
        string = string.replace("LEGÍTIMO", "LEVÍTICO");
        string = string.replace("LEVI TICO", "LEVÍTICO");
        string = string.replace("EXTRA", "ESDRAS");
        string = string.replace("OZÉIAS", "OSÉIAS");
        string = string.replace("AMOR", "AMÓS");
        string = string.replace("OBÁ DIAS", "OBADIAS");
        string = string.replace("CUBA DIAS", "OBADIAS");
        string = string.replace("MIQUEIAS", "MIQUÉIAS");
        string = string.replace("NÃO", "NAUM");
        string = string.replace("DA UM", "NAUM");
        string = string.replace("ÁGIL", "AGEU");
        string = string.replace("FILEMON", "FILEMOM");
        string = string.replace("THIAGO", "TIAGO");
        string = string.replace("PEDRAS", "ESDRAS");
        string = string.replace("MATHEUS", "MATEUS");
        string = string.replace("ESDRA", "ESDRAS");

        String[] s = string.trim().split(" ");
        for (int i = 0; i < arrayLivros.size(); i++) {
            if (string.contains(arrayLivros.get(i))) {
                //  Toast.makeText(getApplicationContext(), arrayLivros.get(i), Toast.LENGTH_SHORT).show();
                livro = arrayLivros.get(i);

            }
        }
        for (int i = 0; i < s.length; i++) {
            if (s[i].trim().matches("^[0-9]*$")) {
                digitos.add(Integer.parseInt(s[i]));
            }

        }
        if (digitos.size() == 2) {
            capitulo = digitos.get(0);
            versiculo = digitos.get(1);
        } else if (digitos.size() == 1) {
            capitulo = digitos.get(0);
        }


        readFromFile(livro, capitulo, versiculo);
        Toast.makeText(this, dita, Toast.LENGTH_SHORT).show();
       // Toast.makeText(this, livro + " " + capitulo + " " + versiculo, Toast.LENGTH_SHORT).show();
    }

    private void proximo() {
        if (versiculo <= arrayList.size() - 2) {
            textLivro.setText(livro + " " + capitulo + ":" + ++versiculo);
            textVersiculo.setText(arrayList.get(versiculo).replaceAll("[0-9]", ""));
        }

    }

    private void anterior() {
        if (versiculo >= 2) {
            textLivro.setText(livro + " " + capitulo + ":" + --versiculo);
            textVersiculo.setText(arrayList.get(versiculo).replaceAll("[0-9]", ""));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         Toast.makeText(getApplicationContext(), String.valueOf(keyCode), Toast.LENGTH_SHORT).show();
        if (keyCode == 66 || keyCode == 30) {//30
            speechRecognizer.startListening(speechRecognizerIntent);

            //Toast.makeText(this, "Fale o livro, capitulo, versiculo", Toast.LENGTH_SHORT).show();
        } else if (keyCode == 24 || keyCode == 92) {//92
            anterior();
        } else if (keyCode == 25 || keyCode == 93) {//93
           proximo();
        }
        return true;
    }
}