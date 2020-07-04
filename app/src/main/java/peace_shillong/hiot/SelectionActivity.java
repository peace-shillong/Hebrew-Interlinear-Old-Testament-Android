package peace_shillong.hiot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import peace_shillong.model.BookNavigator;
import peace_shillong.model.DatabaseManager;

public class SelectionActivity extends AppCompatActivity {

    private Spinner spinnerChapters;
    private Spinner spinnerBooks;
    private Spinner spinnerVerses;
    private SQLiteDatabase database;
    private BookNavigator navigator;
    private String book;
    private int chapter=0;
    private int verse=0;
    private Button buttonGo;
    private TextView permissionText;
    private FirebaseAnalytics mFirebaseAnalytics;

    private SQLiteDatabase initializeDatabase(Context context) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        SQLiteDatabase database = null;

        try {
            //dataBaseHelper.createDataBase();
            database = dataBaseHelper.getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return database;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        try {
            DatabaseManager.init(SelectionActivity.this);
            DatabaseManager instance = DatabaseManager.getInstance();

            if(database==null) {
                database = initializeDatabase(SelectionActivity.this);
                Log.d("Database","INIT");
            }

            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(SelectionActivity.this);

//            Button crashButton = new Button(SelectionActivity.this);
//            crashButton.setText("Crash!");
//            crashButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    throw new RuntimeException("Test Crash"); // Force a crash
//                }
//            });
//
//            addContentView(crashButton, new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "TEST");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            navigator = new BookNavigator(database);

            //request permission
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                //Log.d("Load","Permission");
                permissionText = findViewById(R.id.textView_permission);
                Dexter.withActivity(SelectionActivity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                //Permissions Granted
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                /*Permissions not granted*/
                                permissionText.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
            String[] books = new String[] {"Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges", "Ruth", "1 Samuel", "Ezra", "Nehemiah", "Esther", "Job", "Psalm", "Proverbs", "Ecclesiastes", "Solomon", "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", "Malachi"};

            buttonGo = findViewById(R.id.buttonGo);
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);

                    Bundle bundle = new Bundle();
                    //Found Error here in NullPointerException
                    if(book==null)
                        book="Genesis";
                    bundle.putString("book", book);
                    if(chapter == 0)
                        chapter=1;
                    if(verse == 0)
                        verse=1;
                    bundle.putInt("chapter", chapter);
                    bundle.putInt("verse", verse);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });

            Toast.makeText(SelectionActivity.this, "Loading data please wait...", Toast.LENGTH_SHORT).show();
            spinnerBooks = (Spinner) findViewById(R.id.spinnerbooks);
            spinnerVerses = (Spinner) findViewById(R.id.spinnerverses);
            spinnerChapters = (Spinner) findViewById(R.id.spinnerchapters);

            if (books != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectionActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, books);
                spinnerBooks.setAdapter(adapter);
            } else if ((books = navigator.getBooks()).length > 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectionActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, books);
                spinnerBooks.setAdapter(adapter);
            } else {
                books = navigator.getBooks();
                Toast.makeText(SelectionActivity.this, "Unable to load all the books from the app", Toast.LENGTH_SHORT).show();
            }

            //Put it here to fix the spinners
            //String[] tempFix =navigator.getBooks();

            List<String> list = navigator.getChapters("Genesis"); //temporary fix for Chapters in Genesis on Nokia Phones
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(SelectionActivity.this, android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerChapters.setAdapter(dataAdapter);

            spinnerBooks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object itemAtPosition = parent.getItemAtPosition(position);

                    book = (String) itemAtPosition;
                    List<String> list = navigator.getChapters("Genesis"); //temporary fix for Genesis on Nokia Phones
                    if (view != null) {
                        list = navigator.getChapters(book);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(SelectionActivity.this, android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerChapters.setAdapter(dataAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spinnerChapters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object itemAtPosition = parent.getItemAtPosition(position);

                    try {
                        chapter = Integer.parseInt(itemAtPosition.toString());
                    } catch (NumberFormatException exception) {
                        exception.printStackTrace();
                        return;
                    }

                    List<String> list = navigator.getVerses(book, chapter);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(SelectionActivity.this, android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerVerses.setAdapter(dataAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spinnerVerses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object itemAtPosition = parent.getItemAtPosition(position);

                    try {
                        verse = Integer.parseInt((String) itemAtPosition);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            //System.out.println(tempFix[0]);
        }catch (Exception e)
        {
            //e.printStackTrace();
            Toast.makeText(SelectionActivity.this, "Error  "+ e.getMessage() +" - "+Arrays.toString(e.getStackTrace()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection, menu);
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
