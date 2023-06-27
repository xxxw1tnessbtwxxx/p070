package com.example.p070;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class updtheatres extends AppCompatActivity {
    TextView tvInfo;
    EditText tvName;
    Button tvButton;
    Button tvButtonU;
    Button tvButtonD;
    updtheatres.MyTask mt;

    updtheatres.MyTaskU mtu;
    updtheatres.MyTaskD mtd;
    //    ArrayList<updtheatres.MyTaskU>mtu=new ArrayList <updtheatres.MyTaskU>();
//    ArrayList<updtheatres.MyTaskD>mtd=new ArrayList <updtheatres.MyTaskD>();
    ListView lvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updtheatres);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvName = (EditText) findViewById(R.id.editTextMask);
        lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        tvButton = (Button) findViewById(R.id.tvButtonF);
        tvButtonU = (Button) findViewById(R.id.tvButtonU);
        tvButtonD = (Button) findViewById(R.id.tvButtonD);

    }

    public void onclickF(View v) {
        mt = new updtheatres.MyTask();
        mt.execute(tvName.getText().toString());
    }

    class MyTask extends AsyncTask<String, Void, ArrayList<String[]>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvInfo.setText("Begin");
        }

        @Override
        protected ArrayList<String[]> doInBackground(String... params) {
            ArrayList<String[]> res = new ArrayList<>();
            HttpURLConnection myConnection = null;
            String line = null;
            String total = "";
            try {
                URL githubEndpoint = new URL("http://10.0.2.2:8080/kino/filtrtheatre?name=" + params[0]);
                myConnection =
                        (HttpURLConnection) githubEndpoint.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                tvInfo.setText("1");

            } catch (IOException e) {
                e.printStackTrace();
                tvInfo.setText("2");

            }

            int i = 0;
            try {
                i = myConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 200) {
                InputStream responseBody = null;
                try {
                    responseBody = myConnection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader r = new BufferedReader(new InputStreamReader(responseBody));
                while (true) {
                    try {
                        if (!((line = r.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    total = total + line;
                }
                JSONArray JA = null;
                try {
                    JA = new JSONArray(total);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < JA.length(); j++) {
                    JSONObject JO = null;
                    try {
                        JO = JA.getJSONObject(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String[] st = new String[3];
                    try {
                        st[0] = JO.getString("name").toString();
                        st[1] = JO.getString("address").toString();
                        st[2] = JO.getString("id").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    res.add(st);
                }
            }
            myConnection.disconnect();
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> result) {

            super.onPostExecute(result);
            updtheatres.ClAdapter clAdapter = new updtheatres.ClAdapter(tvInfo.getContext(), result);
            lvMain = (ListView) findViewById(R.id.lvMain);
            lvMain.setAdapter(clAdapter);
//            int n=lvMain.getChildCount();
            tvButtonU.setEnabled(true);
            tvButtonD.setEnabled(true);
            tvButton.setEnabled(false);
        }
    }

    class ClAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        List<String[]> lines;

        ClAdapter(Context context, List<String[]> elines) {
            ctx = context;
            lines = elines;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return lines.size();
        }

        @Override
        public Object getItem(int position) {
            return lines.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.itemupd, parent, false);
            }
            ;
            String[] p = (String[]) getItem(position);
            ((TextView) view.findViewById(R.id.tvText)).setText(p[0]);
            ((TextView) view.findViewById(R.id.tvText1)).setText(p[1]);

            return view;
        }

        public boolean getCheck(int position) {

            return true;
        }
    }

    public void onclickU(View v) {
        int n = lvMain.getChildCount();
        int m = 0;
        JSONArray JA = new JSONArray();
        for (int i = 0; i < n; i++) {
            String[] st = (String[]) lvMain.getAdapter().getItem(i);
            LinearLayout ll = (LinearLayout) lvMain.getChildAt(i);
            CheckBox ch = (CheckBox) ll.getChildAt(0);
            EditText etn = (EditText) ll.getChildAt(1);
            EditText eta = (EditText) ll.getChildAt(2);
            if (ch.isChecked()) {
                String nm = etn.getText().toString();
                String ad = eta.getText().toString();
                try {
                    JA.put(m, new JSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JA.getJSONObject(m).put("name", nm);
                    JA.getJSONObject(m).put("address", ad);
                    JA.getJSONObject(m).put("id", st[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m++;
            }
        }
        mtu = new MyTaskU();
        mtu.execute(JA);
    }

    class MyTaskU extends AsyncTask<JSONArray, Void, String> {

        @Override
        protected String doInBackground(JSONArray... jsonArrays) {
            String line = null;
            String total = null;
            ArrayList<String[]> res = new ArrayList<>();
            BufferedOutputStream os = null;
            HttpURLConnection myConnection = null;
            JSONObject obj = null;
            try {
                URL githubEndpoint = new URL("http://10.0.2.2:8080/kino/update_theatre");
                myConnection =
                        (HttpURLConnection) githubEndpoint.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myConnection.setRequestProperty("Accept",
                    "application/vnd.github.v3+kino");
            myConnection.setRequestProperty("Contact-Me",
                    "hathibelagal@example.com");
            try {
                myConnection.setRequestMethod("PUT");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            myConnection.setDoOutput(true);
            myConnection.setDoOutput(true);
            myConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            myConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            String message = jsonArrays[0].toString();

            try {
                os = new BufferedOutputStream(myConnection.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                os.write(message.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //clean up
            try {
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            int i = 0;
            try {
                i = myConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
//                tvInfo.setText(str);
            if (i == 200) {
                InputStream responseBody = null;
                try {
                    responseBody = myConnection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader r = new BufferedReader(new InputStreamReader(responseBody));
                while (true) {
                    try {
                        if (!((line = r.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    total = total + line;
                }
                JSONArray JA = null;
                try {
                    JA = new JSONArray(total);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < JA.length(); j++) {
                    JSONObject JO = null;
                    try {
                        JO = JA.getJSONObject(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String[] st = new String[2];
                    try {
                        st[0] = JO.getString("result").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    res.add(st);
                }
            }
            return total;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvInfo.setText("End");
            return;
        }
    }

    public void onclickD(View v) {
        int n = lvMain.getChildCount();
        int m = 0;
        JSONArray JA = new JSONArray();
        for (int i = 0; i < n; i++) {
            String[] st = (String[]) lvMain.getAdapter().getItem(i);
            LinearLayout ll = (LinearLayout) lvMain.getChildAt(i);
            CheckBox ch = (CheckBox) ll.getChildAt(0);
            if (ch.isChecked()) {
                try {
                    JA.put(m, new JSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JA.getJSONObject(m).put("id", st[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m++;
            }
            ;
        }
        mtd = new MyTaskD();
        mtd.execute(JA);

    }

    class MyTaskD extends AsyncTask<JSONArray, Void, String> {

        @Override
        protected String doInBackground(JSONArray... jsonArrays) {
            String line = null;
            String total = null;
            ArrayList<String[]> res = new ArrayList<>();
            BufferedOutputStream os = null;
            HttpURLConnection myConnection = null;
            JSONObject obj = null;
            try {
                URL githubEndpoint = new URL("http://10.0.2.2:8080/kino/delete_theatre");
                myConnection =
                        (HttpURLConnection) githubEndpoint.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myConnection.setRequestProperty("Accept",
                    "application/vnd.github.v3+kino");
            myConnection.setRequestProperty("Contact-Me",
                    "hathibelagal@example.com");
            try {
                myConnection.setRequestMethod("DELETE");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            myConnection.setDoOutput(true);
            myConnection.setDoOutput(true);
            myConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            myConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            String message = jsonArrays[0].toString();

            try {
                os = new BufferedOutputStream(myConnection.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                os.write(message.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //clean up
            try {
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int i = 0;
            try {
                i = myConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
//                tvInfo.setText(str);
            if (i == 200) {
                InputStream responseBody = null;
                try {
                    responseBody = myConnection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader r = new BufferedReader(new InputStreamReader(responseBody));
                while (true) {
                    try {
                        if (!((line = r.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    total = total + line;
                }
                JSONArray JA = null;
                try {
                    JA = new JSONArray(total);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < JA.length(); j++) {
                    JSONObject JO = null;
                    try {
                        JO = JA.getJSONObject(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String[] st = new String[2];
                    try {
                        st[0] = JO.getString("result").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    res.add(st);
                }
            }
            return total;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvInfo.setText("End");
            return;
        }
    }
} 
