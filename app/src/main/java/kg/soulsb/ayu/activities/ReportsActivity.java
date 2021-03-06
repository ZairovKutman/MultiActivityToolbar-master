package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.ReportsAdapter;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.ReportInput;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.grpctest.nano.ReportOutput;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ReportsRepo;
import kg.soulsb.ayu.helpers.repo.SavedReportsRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Report;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ReportsActivity extends BaseActivity {
    ArrayList<Report> reports;
    int mYear, mMonth, mDay;
    private ListView listView;
    EditText endDate;
    EditText startDate;
    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;
    boolean shouldDownload = true;

    AlertDialog.Builder d2;
    ProgressBar progressBar;
    AlertDialog alertDialog;

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);



        listView = (ListView) findViewById(R.id.listView);

        DBHelper dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);

        reports = new ReportsRepo().getReportsObject();

        ReportsAdapter arrayAdapter = new ReportsAdapter(this,R.layout.reports_list,reports);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public Baza baza;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                /////////////////
                final AlertDialog.Builder d = new AlertDialog.Builder(ReportsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.datepicker_dialog, null);


                startDate = (EditText) dialogView.findViewById(R.id.dateStart);
                endDate = (EditText) dialogView.findViewById(R.id.dateEnd);

                startDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentDate = Calendar.getInstance();
                        mYear = mcurrentDate.get(Calendar.YEAR);
                        mMonth = mcurrentDate.get(Calendar.MONTH);
                        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog mDatePicker = new DatePickerDialog(dialogView.getContext(), new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, selectedyear);
                                myCalendar.set(Calendar.MONTH, selectedmonth);
                                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                                String myFormat = "dd/MM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                                startDate.setText(sdf.format(myCalendar.getTime()));

                                mDay = selectedday;
                                mMonth = selectedmonth;
                                mYear = selectedyear;
                            }
                        }, mYear, mMonth, mDay);
                        mDatePicker.setTitle("???????????????? ???????????? ??????????????");
                        mDatePicker.show();
                    }
                });

                endDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentDate = Calendar.getInstance();
                        mYear = mcurrentDate.get(Calendar.YEAR);
                        mMonth = mcurrentDate.get(Calendar.MONTH);
                        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog mDatePicker = new DatePickerDialog(dialogView.getContext(), new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, selectedyear);
                                myCalendar.set(Calendar.MONTH, selectedmonth);
                                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                                String myFormat = "dd/MM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                                endDate.setText(sdf.format(myCalendar.getTime()));

                                mDay = selectedday;
                                mMonth = selectedmonth;
                                mYear = selectedyear;
                            }
                        }, mYear, mMonth, mDay);
                        mDatePicker.setTitle("???????????????? ?????????? ??????????????");
                        mDatePicker.show();
                    }
                });

                d.setTitle(reports.get(position).getName());
                d.setMessage("???????????????? ????????????");
                d.setView(dialogView);

                d.setPositiveButton("??????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        shouldDownload = true;
                        DBHelper dbHelper = new DBHelper(getBaseContext());
                        DatabaseManager.initializeInstance(dbHelper);
                        ArrayList<Report> arrayListSavedreports = new SavedReportsRepo().getReportsObject();
                        for (final Report report1: arrayListSavedreports)
                        {
                            if (report1.getGuid().equals(reports.get(position).getGuid()) && report1.getDateStart().equals(startDate.getText().toString()) && report1.getDateEnd().equals(endDate.getText().toString()))
                            {
                                shouldDownload = false;
                                final AlertDialog.Builder d1 = new AlertDialog.Builder(ReportsActivity.this);
                                LayoutInflater inflater1 = getLayoutInflater();
                                final View dialogView1 = inflater1.inflate(R.layout.are_you_sure_dialog, null);
                                d1.setTitle("??????????????????????");
                                d1.setMessage("?????????????????? ?????????????????????? ???????????");
                                d1.setView(dialogView1);

                                d1.setPositiveButton("????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.out.println("?????????????????????? ?????????? +++++!+!+!+!+!+!+!");
                                        Intent intent = new Intent(getBaseContext(), ReportsDetailActivity.class);
                                        intent.putExtra("decoded",report1.getContentHTML());
                                        startActivity(intent);

                                    }
                                });

                                d1.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        d2 = new AlertDialog.Builder(ReportsActivity.this);
                                        LayoutInflater inflater = getLayoutInflater();
                                        View dialogView = inflater.inflate(R.layout.loading_dialog, null);
                                        progressBar = (ProgressBar) dialogView.findViewById(R.id.loading_bar);

                                        d2.setTitle("???????????????? ????????????");
                                        d2.setMessage("??????????????????...");
                                        d2.setView(dialogView);
                                        alertDialog = d2.create();
                                        alertDialog.show();
                                        downloadReport(baza, position);
                                    }
                                });

                                AlertDialog alertDialog1 = d1.create();
                                alertDialog1.show();
                                break;
                            }
                        }

                        if (shouldDownload) {
                            d2 = new AlertDialog.Builder(ReportsActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.loading_dialog, null);
                            progressBar = (ProgressBar) dialogView.findViewById(R.id.loading_bar);

                            d2.setTitle("???????????????? ????????????");
                            d2.setMessage("??????????????????...");
                            d2.setView(dialogView);
                            alertDialog = d2.create();
                            alertDialog.show();

                            downloadReport(baza,position);
                        }

                    }
                });

                d.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog alertDialog = d.create();
                alertDialog.show();

                /////////////////
            }
        });

    }

    public void downloadReport(Baza baza, int position)
    {
        baza = CurrentBaseClass.getInstance().getCurrentBaseObject();

        String mHost = baza.getHost();
        int mPort = baza.getPort();
        System.out.println("???????????????? +++++!+!+!+!+!+!+!");

        ReportsActivity.GrpcTask grpcTask = new ReportsActivity.GrpcTask(ManagedChannelBuilder.forAddress(mHost, mPort)
                .usePlaintext(true).build(), CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent(), reports.get(position).getGuid());
        grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private class GrpcTask extends AsyncTask<Void, Void, Points> {
        private ManagedChannel mChannel;
        private String name;
        private String reportGuid;

        public GrpcTask(ManagedChannel mChannel, String name, String reportGuid) {
            this.mChannel = mChannel;
            this.name = name;
            this.reportGuid = reportGuid;
        }

        /**
         * ?????????? ?????????????????????? ?????????? ?????????????? ???????????? AsyncTask
         */
        @Override
        protected void onPreExecute() {
        }

        private Points sendReportDetails(ManagedChannel mChannel) {
            blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);

            Agent agent = new Agent();
            agent.name = name;

            ReportInput request = new ReportInput();
            request.agent = agent;
            request.dateStart = startDate.getText().toString();
            request.dateEnd = endDate.getText().toString();
            request.reportGuid = reportGuid;

            ReportOutput reportOutput = blockingStub.getReport(request);

            String decoded ="";
            try {
                decoded = new String(reportOutput.reportFile, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getBaseContext(), ReportsDetailActivity.class);
            intent.putExtra("decoded",decoded);
            Report report = new Report(reportGuid, reportOutput.description);

            report.setContentHTML(decoded);
            report.setDateStart(startDate.getText().toString());
            report.setDateEnd(endDate.getText().toString());
            report.setBase(CurrentBaseClass.getInstance().getCurrentBase());

            SavedReportsRepo savedReportsRepo = new SavedReportsRepo();

            savedReportsRepo.delete(report);
            savedReportsRepo.insert(report);

            startActivity(intent);

            return null;
        }

        /**
         * ?????????? ???????????????????????? ?????? ?? ?????????????? ????????????.
         *
         * @param nothing
         * @return
         */
        @Override
        protected Points doInBackground(Void... nothing) {
            try {
                return sendReportDetails(mChannel);
            } catch (Exception e) {
                e.printStackTrace();
                return
                        // ???????????????????? ???????????? ????????????????
                        null;
            }
        }

        @Override
        protected void onPostExecute(Points pointIterator) {
            try {
                mChannel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

}
