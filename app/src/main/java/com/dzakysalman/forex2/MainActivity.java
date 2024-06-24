package com.dzakysalman.forex2;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout _swipeRefreshLayout1;
    private RecyclerView _recyclerView1;
    private TextView _timestampTextView;
    private Map<String, String> currencyMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSwipeRefreshLayout();
        _recyclerView1 = findViewById(R.id.recyclerView1);
        _timestampTextView = findViewById(R.id.timestampTextView);

        bindRecyclerView();
    }

    private void bindRecyclerView() {
        String url = "https://openexchangerates.org/api/latest.json?app_id=19e6e6f29fe0419ea2afc01e34c707ef";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonString = new String(responseBody);
                JSONObject root;

                try {
                    root = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject rates;
                long timestamp;

                try {
                    rates = root.getJSONObject("rates");
                    timestamp = root.getLong("timestamp");
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mengatur tanggal dan waktu dari timestamp API
                setTimestamp(timestamp);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                ForexAdapter adapter = new ForexAdapter(rates);
                _recyclerView1.setLayoutManager(layoutManager);
                _recyclerView1.setAdapter(adapter);

                _swipeRefreshLayout1.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, new String(responseBody), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setTimestamp(long timestamp) {
        // Mengambil waktu dalam zona waktu UTC
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateTime = format.format(new Date(timestamp * 1000));

        // Set tanggal dan waktu ke TextView
        _timestampTextView.setText("Tanggal dan Waktu (UTC): " + dateTime);
    }
    private void initSwipeRefreshLayout() {
        _swipeRefreshLayout1 = findViewById(R.id.swipeRefreshLayout1);

        _swipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { bindRecyclerView();}
        });
    }
}