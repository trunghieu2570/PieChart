package kdh.demo.piechart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final Button updateButton = findViewById(R.id.btn_update);
        final Button updateTogetherButton = findViewById(R.id.btn_update_together);
        final Button updateSequentiallyButton = findViewById(R.id.btn_update_sequentially);
        final EditText valuesEditText = findViewById(R.id.edt_values);
        final PieChart pieChart = findViewById(R.id.pieChart);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.updateAngle(convertStringToIntArray(valuesEditText.getText().toString()),
                        PieChart.AnimType.NO_ANIMATION);
            }
        });
        updateTogetherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.updateAngle(convertStringToIntArray(valuesEditText.getText().toString()),
                        PieChart.AnimType.TOGETHER);
            }
        });
        updateSequentiallyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChart.updateAngle(convertStringToIntArray(valuesEditText.getText().toString()),
                        PieChart.AnimType.SEQUENTIALLY);
            }
        });
    }

    private int[] convertStringToIntArray(String str) {
            String[] tmp = str.split(" ");
            int[] result = new int[tmp.length];
            for(int i = 0; i < result.length; i++) {
                result[i] = Integer.parseInt(tmp[i]);
            }
            return result;
    }
}
