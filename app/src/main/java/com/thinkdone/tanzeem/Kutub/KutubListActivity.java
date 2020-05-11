package com.thinkdone.tanzeem.Kutub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.thinkdone.tanzeem.BookMarks.BookMarksActivity;
import com.thinkdone.tanzeem.Categories.CategoriesActivity;
import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.DB.DBHelper;
import com.thinkdone.tanzeem.DB.KitabDBHelper;
import com.thinkdone.tanzeem.DBFiles.KutubDBHelperFile;
import com.thinkdone.tanzeem.DataModels.KitabDataModel;
import com.thinkdone.tanzeem.Drawer.About_us;
import com.thinkdone.tanzeem.MainActivity;
import com.thinkdone.tanzeem.R;
import com.thinkdone.tanzeem.Updates.UpdatesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class KutubListActivity extends AppCompatActivity {

    Activity activity = this;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch ( item.getItemId()) {
                case R.id.fist_page:
                    startActivity(new Intent(activity, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case R.id.kutub:
                    startActivity(new Intent(activity, KutubListActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case R.id.bookmark:
                    startActivity(new Intent(activity, BookMarksActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case R.id.topic:
                    startActivity(new Intent(activity, CategoriesActivity.class));
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;
                case R.id.download:
                    Intent intent = new Intent(activity, UpdatesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    break;

            }
            return  true;
        };
    };
    int[] categoriesId = {-1,11,12,13,14,15,16,17,18,19,20};
    String query;
    Spinner spinner;
    RecyclerView kutubRecycler;
    private static final float END_SCALE = 0.7f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_books);
        progressBar = findViewById(R.id.progressBar2);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navigation.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        if(getIntent().getExtras() != null && getIntent().getExtras().getInt(Constants.CATEGORY,0) != 0){
            Log.d("tryCate",getIntent().getExtras().getInt(Constants.CATEGORY,0)+"");
        }
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item_layout,getResources().getStringArray(R.array.categories_names));
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 if(position == 0){
                    query = "Select * from "+ DBHelper.KitabEntry.TABLE_NAME;
                }else {
                    query = "Select * from " + DBHelper.KitabEntry.TABLE_NAME + " where " + DBHelper.KitabEntry.CATEGORY_ID + " = " + categoriesId[position];
                }
                Log.d("tryKutub",query +"   "+position);
                loadDataFromDB(query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(getIntent().getExtras() != null && getIntent().getExtras().getInt(Constants.CATEGORY,0) != 0){
            int cid = getIntent().getExtras().getInt(Constants.CATEGORY,0);
            query = "Select * from " + DBHelper.KitabEntry.TABLE_NAME + " where " + DBHelper.KitabEntry.CATEGORY_ID + " = " + cid;
            for(int i=0;i<categoriesId.length;i++)
            {
                if(cid == categoriesId[i])
                {
                    spinner.setSelection(i);
                    break;
                }
            }
        }

        kutubRecycler = findViewById(R.id.kutub_recycler);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        kutubRecycler.setLayoutManager(linearLayoutManager);
        handler = new Handler();
//        query = "Select * from "+ DBHelper.KitabEntry.TABLE_NAME;
//        loadDataFromDB(query);


        //TODO navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.homed) {
                    Intent intent = new Intent(KutubListActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.aboutd) {
                    Intent intent = null;
                    intent = new Intent(KutubListActivity.this, About_us.class);
                    intent.putExtra("title","کچھ ہمارے بارے میں");
                    intent.putExtra("type", "about");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.booksd) {
                    Intent intent = new Intent(KutubListActivity.this, KutubListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.tawund) {
                    Intent intent = null;
                    intent = new Intent(KutubListActivity.this, About_us.class);
                    intent.putExtra("title","تعاون");
                    intent.putExtra("type", "tawun");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else if (id == R.id.rabtad) {
                    Intent intent = null;
                    intent = new Intent(KutubListActivity.this, About_us.class);
                    intent.putExtra("title","رابطہ");
                    intent.putExtra("type", "rabta");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    // Intent intent = new Intent(MainActivity.this, AboutApp.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageView layout = findViewById(R.id.imageView);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        //TODO header close button code
        View headerV = navigationView.inflateHeaderView(R.layout.header_drawer);
        ImageView close = headerV.findViewById(R.id.imageView25);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        final ConstraintLayout contentView = findViewById(R.id.mainll);
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawerView, float slideOffset) {
                                         //labelView.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);
                                         // Scale the View based on current slide offset
                                         final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                         final float offsetScale = 1 - diffScaledOffset;
                                         contentView.setScaleX(offsetScale);
                                         contentView.setScaleY(offsetScale);

                                         // Translate the View, accounting for the scaled width
                                         final float xOffset = drawerView.getWidth() * slideOffset;
                                         final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                         final float xTranslation = xOffset - xOffsetDiff;
                                         contentView.setTranslationX(xTranslation);
                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                         //labelView.setVisibility(View.GONE);
                                     }
                                 }
        );
        //TODO make menu item appear in center
        Menu menud = navigationView.getMenu();
        for(int i = 0; i < menud.size(); i++) {
            MenuItem item = menud.getItem(i);
            if(item.getTitle() != null) {
                SpannableString s = new SpannableString(item.getTitle());
                s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
                item.setTitle(s);
            }
        }
        drawer.setStatusBarBackgroundColor(Color.parseColor("#292118"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#292118"));
        }

        final ImageView layoutd = findViewById(R.id.imageView);
        layoutd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });


    }

    Handler handler;
    KutubAdapter kutubAdapter;
    ProgressBar progressBar;
    public void loadDataFromDB(final String query2)
    {
        new AsyncTask<Void,Void,Void>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                kutubAdapter = new KutubAdapter(getApplicationContext(),KutubListActivity.this);
                kutubRecycler.setAdapter(kutubAdapter);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {

                ArrayList<String> dbList = KutubDBHelperFile.getDatabasesList();
                for(int ij=-1;ij<dbList.size();ij++) {
                    if(ij == -1) {
                        KitabDBHelper kitabDBHelper = new KitabDBHelper(getApplicationContext());
                        SQLiteDatabase sqLiteDatabase = kitabDBHelper.getReadableDatabase();
                        Log.d("tryQuery",query2);
                        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            if(cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.IS_ACTIVE)) == 1) {
                                final KitabDataModel kitabDataModel = new KitabDataModel();
                                kitabDataModel.setBookId(cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.ID)));
                                kitabDataModel.setKitabNameEng(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME)));
                                kitabDataModel.setBookName(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME_URDU)));
                                kitabDataModel.setBookImage(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.IMAGE_NAME)));
                                Log.d("tryCat", "val = " + kitabDataModel.getBookName());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                            kutubAdapter.addKitab(kitabDataModel);
                                            kutubAdapter.notifyDataSetChanged();

                                    }
                                });
                            }
                        }
                        cursor.close();
                    }else{
                        KutubDBHelperFile kutubDBHelperFile = new KutubDBHelperFile(getApplicationContext(),dbList.get(ij));
                        SQLiteDatabase sqLiteDatabase = kutubDBHelperFile.getWritableDatabase();
                        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            final KitabDataModel kitabDataModel = new KitabDataModel();
                            kitabDataModel.setBookId(cursor.getInt(cursor.getColumnIndex(DBHelper.KitabEntry.ID)));
                            kitabDataModel.setKitabNameEng(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME)));
                            kitabDataModel.setBookName(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.NAME_URDU)));
                            kitabDataModel.setBookImage(cursor.getString(cursor.getColumnIndex(DBHelper.KitabEntry.IMAGE_NAME)));
                            Log.d("tryCat2", "val = " + kitabDataModel.getBookName());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    kutubAdapter.addKitab(kitabDataModel);
                                    kutubAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        cursor.close();

                    }
                }
                return null;
            }

        }.execute();

    }

    public void backBtn(View v)
    {
        onBackPressed();
    }

}