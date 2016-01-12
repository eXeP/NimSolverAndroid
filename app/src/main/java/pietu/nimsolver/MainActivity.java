package pietu.nimsolver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<Integer> stacks = new ArrayList<>();
    FlowLayout flow;
    TextView statustxt, infotext;
    CardView textHolder;
    String status = "Add stacks to solve", info = "You can remove stacks by clicking on them.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flow = (FlowLayout)findViewById(R.id.flow);
        statustxt = (TextView)findViewById(R.id.status_text);
        infotext = (TextView)findViewById(R.id.info_text);
        textHolder = (CardView)findViewById(R.id.card_view);
        ShowStacks();
    }

    private void SolveNim(){
        if(stacks.size() < 1){
            status = "Add stacks to solve";
            info = "You can remove stacks by clicking on them.";
            statustxt.setText(status);
            infotext.setText(info);
            textHolder.setBackgroundColor(getResources().getColor(R.color.white));
            return;
        }
        int xorsum = 0;
        for(int i: stacks){
            xorsum^=i;
        }
        if(xorsum == 0){
            status = "You can't win";
            info = "If your opponent plays optimally, you can't win. You can do any move, try to shake up your opponent.";
            textHolder.setBackgroundColor(getResources().getColor(R.color.lightRed));
        }
        else{
            status = "You will win";
            int highBitIdx = calcHighestBitIndex(xorsum), stackn = 0;

            for(int i: stacks){
                if((i&(1<<highBitIdx)) > 0){
                    stackn = i;
                    break;
                }
            }
            info = "In order to win, you must first remove " + (stackn-(stackn^xorsum)) + " from a stack with " + stackn + " elements in it.";
            textHolder.setBackgroundColor(getResources().getColor(R.color.lightGreen));
        }

        statustxt.setText(status);
        infotext.setText(info);
    }

    private int calcHighestBitIndex(int xs){
        int highBitIndex = 0;
        for(highBitIndex = 31; highBitIndex >= 0; highBitIndex--){
            if((xs&(1<<highBitIndex)) > 0)
                break;
        }
        return highBitIndex;
    }
    private void ShowStacks(){
        flow.removeAllViews();
        for(Integer i: stacks){

            TextView add = (TextView) View.inflate(this, R.layout.numb_textview, null);
            FlowLayout.LayoutParams params= new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 20, 0); // change this to your liking
            add.setLayoutParams(params);
            add.setText(i.toString());
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView)v;
                    stacks.remove(stacks.indexOf(Integer.parseInt(tv.getText().toString())));
                    ShowStacks();
                }
            });
            flow.addView(add);
        }
        TextView add = (TextView) View.inflate(this, R.layout.numb_textview, null);
        FlowLayout.LayoutParams params= new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 20, 0); // change this to your liking
        add.setLayoutParams(params);
        add.setText("add stack");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("New stack");


                final EditText input = new EditText(getApplicationContext());

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (!isInteger(m_Text)) {
                            Toast.makeText(MainActivity.this, "Invalid input",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        stacks.add(Integer.parseInt(m_Text));
                        ShowStacks();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        flow.addView(add);
        SolveNim();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
