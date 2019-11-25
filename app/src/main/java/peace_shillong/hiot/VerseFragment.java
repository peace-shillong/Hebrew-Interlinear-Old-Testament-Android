package peace_shillong.hiot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
//import crl.android.pdfwriter.PDFWriter;
//import crl.android.pdfwriter.PaperSize;
//import crl.android.pdfwriter.StandardFonts;
import peace_shillong.model.DatabaseManager;
import peace_shillong.model.Word;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class VerseFragment extends Fragment {

    private String book;
    private int chapter;
    private int verse;
    private List<Word> words;
    private Typeface typeface;
    private ActivityObjectProvider provider;
    private View contentView;
    private String hebrew="",data="",english="";
    private ScrollView screen;
    private FlowLayout layout;
    private Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static VerseFragment newInstance(String book, int chapter, int verse) {
        VerseFragment fragment = new VerseFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();

        args.putString("book", book);
        args.putInt("chapter", chapter);
        args.putInt("verse", verse);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try
        {
            provider = (ActivityObjectProvider) activity;
        } catch(ClassCastException e) {
            throw new RuntimeException("it ain't a Provider");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        book = getArguments().getString("book");
        chapter = getArguments().getInt("chapter");
        verse = getArguments().getInt("verse");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser) {
            FragmentActivity activity = getActivity();
            if(activity != null)
            {
                ((MainActivity)activity).setActionBarTitle(String.format("%s %d:%d", book, chapter, verse));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        contentView = inflater.inflate(R.layout.activity_fragment, null);
        contentView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        setHasOptionsMenu(true);
        typeface = provider.getTypeface();
        words = provider.getWords(verse);

        layout = new FlowLayout(context, null);
        layout.setLayoutParams(
                new FlowLayout.LayoutParams(
                        FlowLayout.LayoutParams.MATCH_PARENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT
                ));
        layout.setPadding(10, 10, 10, 10);

        List<Word> words = provider.getWords(verse);
        if(words == null) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(32, 32, 32, 32);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            TextView textViewErrorText = new TextView(context);
            textViewErrorText.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            textViewErrorText.setText(context.getString(R.string.missing_verse_text));

            TextView textViewErrorLink = new TextView(context);
            //textViewErrorLink.setText(context.getString(R.string.missing_verse_link));
            textViewErrorLink.setText(Html.fromHtml("For more information <a href='http://en.wikipedia.org/wiki/List_of_Bible_verses_not_included_in_modern_translations'>this link</a>"));
            textViewErrorLink.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            textViewErrorLink.setMovementMethod(LinkMovementMethod.getInstance());

            linearLayout.addView(textViewErrorText);
            linearLayout.addView(textViewErrorLink);

            ((ViewGroup) contentView).addView(linearLayout);
        }
        else {
            int count = words.size();
            for (int i = 0; i < count; i++) {

                LinearLayout linearLayout = getLayout(context, i);

                layout.addView(linearLayout);
                //generatePdf();
            }
            ((ViewGroup) contentView).addView(layout);
        }
        //added
        screen=contentView.findViewById(R.id.screen);
        return contentView; // super.onCreateView(inflater, container, savedInstanceState);
    }

    private LinearLayout getLayout(Context context, int index) {

        Bundle prefs = provider.getPreferences();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(10, 20, 30, 40);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        final TextView textViewStrongs = new TextView(context);
        textViewStrongs.setTag("textViewStrongs" + index);
        textViewStrongs.setTextAppearance(context, android.R.style.TextAppearance_Small);
        textViewStrongs.setGravity(Gravity.RIGHT);
        textViewStrongs.setTextColor(Color.rgb(77, 179, 179));
        textViewStrongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textViewStrongs.getText().toString();
                String[] parts = text.split("\\&");

                DatabaseManager manager = DatabaseManager.getInstance();
                String strongs = manager.getStrongs(parts);

                Toast toast = Toast.makeText(v.getContext(), strongs, Toast.LENGTH_LONG);
                toast.show();
            }
        });

        String fontSizeWord = prefs.getString("font_size_word", "2");
        int fontResId = 0;

        switch(fontSizeWord) {
            case "0": // small
                fontResId = android.R.style.TextAppearance_Small;
                break;
            case "1": // medium
                fontResId = android.R.style.TextAppearance_Medium;
                break;
            case "2": // medium
                fontResId = android.R.style.TextAppearance_Large;
                break;
        }

        TextView textViewWord = new TextView(context);
        textViewWord.setTextAppearance(context, fontResId);
        textViewWord.setTypeface(typeface);
        textViewWord.setTextColor(Color.rgb(61, 76, 83));
        textViewWord.setTag("textViewWord" + index);

        TextView textViewConcordance = new TextView(context);
        textViewConcordance.setTextAppearance(context, android.R.style.TextAppearance_Holo_Small);
        textViewConcordance.setGravity(Gravity.RIGHT);
        textViewConcordance.setTextColor(Color.rgb(230, 74, 69));
        textViewConcordance.setTag("textViewConcordance" + index);

        TextView textViewTransliteration = new TextView(context);
        textViewTransliteration.setTextAppearance(context, android.R.style.TextAppearance_Small);
        textViewTransliteration.setTextColor(Color.rgb(77, 179, 179));
        textViewTransliteration.setGravity(Gravity.RIGHT);
        textViewTransliteration.setTag("textViewTransliteration" + index);
        /*textViewTransliteration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result = PosParser.get((String) ((TextView) v).getTag());

                Toast toast = Toast.makeText(v.getContext(), result, Toast.LENGTH_LONG);
                toast.show();
            }
        });*/

        TextView textViewLemma = new TextView(context);
        textViewLemma.setTextAppearance(context, android.R.style.TextAppearance_Small);
        textViewLemma.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        textViewLemma.setGravity(Gravity.RIGHT);
        textViewLemma.setTag("textViewLemma" + index);

        textViewStrongs.setText(words.get(index).getStrongs());
        textViewWord.setText(words.get(index).getWord());
        textViewTransliteration.setText(words.get(index).getTranslit());
        textViewConcordance.setText(words.get(index).getConcordance());
        textViewLemma.setText(words.get(index).getLemma());
        Log.d("hebrew data",""+words.get(index).getWord());

        hebrew=hebrew+" - "+words.get(index).getWord();
        english=words.get(index).getTranslit()+" - "+english;
        data=words.get(index).getConcordance()+" - "+data;

        //String pdfcontent=generatePdf(index); to be done in another place
        //outputToFile( book+" Chapter "+chapter+" Verse "+verse+".pdf",pdfcontent,"UTF-8");
        boolean showStrongs = prefs.getBoolean("show_strongs", false);
        if(showStrongs)
            linearLayout.addView(textViewStrongs);

        linearLayout.addView(textViewWord);

        boolean showConcordance = prefs.getBoolean("show_concordance", false);
        if(showConcordance)
            linearLayout.addView(textViewConcordance);

        boolean showFunctional = prefs.getBoolean("show_transliteration", false);
        if(showFunctional)
            linearLayout.addView(textViewTransliteration);

        boolean showLemma = prefs.getBoolean("show_lemma", false);
        if(showLemma)
            linearLayout.addView(textViewLemma);

        return linearLayout;
    }
/*
    private String generatePdf(int index) {
        PDFWriter writer = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.FOLIO_HEIGHT);
        //typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cardo104s.ttf");
        //writer.setFont(StandardFonts.SUBTYPE,typeface,"UTF-8");
        writer.addText(85, 75, 18, hebrew);
        writer.addText(85, 105, 18, english);
        writer.addText(85, 115, 18, data);
        writer.newPage();
        int pageCount = writer.getPageCount();
        for (int i = 0; i < pageCount; i++) {
            writer.setCurrentPage(i);
            writer.addText(10, 10, 8, Integer.toString(i + 1) + " / " + Integer.toString(pageCount));
        }
        String s = writer.asString();
        return s;
    }
    private void outputToFile(String fileName, String pdfContent, String encoding) {
        File downloads = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        if (!downloads.exists() && !downloads.mkdirs())
            throw new RuntimeException("Could not create download folder");
        File newFile = new File(downloads, fileName);
        Log.i("PDF", "Writing file to " + newFile);

        try {
            newFile.createNewFile();
            try {
                FileOutputStream pdfFile = new FileOutputStream(newFile);
                pdfFile.write(pdfContent.getBytes(encoding));
                pdfFile.close();
            } catch(FileNotFoundException e) {
                Log.w("PDF", e);
            }
        } catch(IOException e) {
            Log.w("PDF", e);
        }
    }
*/

    public void screenshot(String action){
        screen.setDrawingCacheEnabled(true);
        try {
            Bitmap bitmap = getBitmapFromView(screen, screen.getChildAt(0).getHeight(), screen.getChildAt(0).getWidth());
            //Create File and  then store in Downloads Folder
            File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "OT "+ book+" Chapter "+chapter+" Verse "+verse+".jpg");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            if(action.equals("share")) {
                //if SDK below else
                //Uri uri = Uri.fromFile(file);
                Uri uri  = FileProvider.getUriForFile(context, "com.peace_shillong.hiot.provider", file);
                //share
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, book+" "+chapter+":"+verse+" in Hebrew");
                startActivity(Intent.createChooser(intent, "Share Verse"));

            }
            //Log.d("LOG", "Image created");
            if(action.equals("export"))
            {
                Toast.makeText(context, "Image Saved in Downloads Folder", Toast.LENGTH_SHORT).show();
            }
            //screen.destroyDrawingCache();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d("LOG", "Image is too large, Please Crop the image with a smaller size");
        }
    }

    //create bitmap from the ScrollView
    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);//added this line of code for bg to be WHITE if screen not scrolled
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Log.d("LOG", "Got called");
        switch (item.getItemId()) {
            case R.id.action_export:
                // Do Fragment menu item stuff here
                screenshot("export");
                return true;
            case R.id.action_share:
                // Do Fragment menu item stuff here
                screenshot("share");
                return true;
            default:
                break;
        }
        return false;
    }
}
