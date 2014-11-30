package com.ajaybhatia.tipcalc;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TOTAL_BILL = "TOTAL_BILL";
	private static final String CURRENT_TIP = "CURRENT_TIP";
	private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";
	
	private double billBeforeTip;
	private double tipAmount;
	private double finalBill;
	
	private int[] checkListValues = new int[12];
	
	private long secondsYouWaited = 0L;
	
	EditText billBeforeTipET;
	EditText tipAmountET;
	EditText finalBillET;
	
	CheckBox friendlyCB;
	CheckBox specialsCB;
	CheckBox opinionCB;
	
	RadioGroup availableRG;
	RadioButton availableBadRB;
	RadioButton availableOKRB;
	RadioButton availableGoodRB;
	
	Spinner problemSP;
	
	Button startChronometerBT;
	Button pauseChronometerBT;
	Button resetChronometerBT;
	
	Chronometer timeWaitingChronometer;
	
	TextView timeWaitingTV;
	
	SeekBar tipSeekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			billBeforeTip = 0.0;
			tipAmount = 0.15;
			finalBill = 0.0;
		} else {
			billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
			tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
			finalBill = savedInstanceState.getDouble(TOTAL_BILL);
		}

		billBeforeTipET = (EditText)findViewById(R.id.billEditText);
		tipAmountET = (EditText)findViewById(R.id.tipEditText);
		finalBillET = (EditText)findViewById(R.id.finalBillEditText);
		
		tipSeekBar = (SeekBar)findViewById(R.id.changeTipSeekBar);
		
		billBeforeTipET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					billBeforeTip = Double.parseDouble(s.toString());
				} catch (NumberFormatException e) {
					billBeforeTip = 0.0;
				}
				
				updateTipAndFinalBill();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		tipSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tipAmount = tipSeekBar.getProgress() * 0.01;
				tipAmountET.setText(String.format("%.2f", tipAmount));
				updateTipAndFinalBill();
			}
		});
		
		friendlyCB = (CheckBox)findViewById(R.id.friendlyCheckBox);
		specialsCB = (CheckBox)findViewById(R.id.specialsCheckBox);
		opinionCB = (CheckBox)findViewById(R.id.opinionCheckBox);
		
		specialUpIntroCheckBoxes();
		
		availableRG = (RadioGroup)findViewById(R.id.availableRadioGroup);
		availableBadRB = (RadioButton)findViewById(R.id.availableBadRadio);
		availableOKRB = (RadioButton)findViewById(R.id.availableOKRadio);
		availableGoodRB = (RadioButton)findViewById(R.id.availableGoodRadio);
		
		addChangeListenerToRadios();
		
		problemSP = (Spinner)findViewById(R.id.problemsSpinner);
		
		addItemSelectedListenerToSpinner();
		
		startChronometerBT = (Button)findViewById(R.id.startChronometerButton);
		pauseChronometerBT = (Button)findViewById(R.id.pauseChronometerButton);
		resetChronometerBT = (Button)findViewById(R.id.resetChronometerButton);
		
		setButtonsOnClickListeners();
		
		timeWaitingChronometer = (Chronometer)findViewById(R.id.timeWaitingChronometer);
		
		timeWaitingTV = (TextView)findViewById(R.id.timeWaitingTextView);
		
	}
	
	private void setButtonsOnClickListeners() {
		startChronometerBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int stoppedMilliseconds = 0;
				String chronoText = timeWaitingChronometer.getText().toString().trim();
				String[] array = chronoText.split(":");
				
				if (array.length == 2) {
					stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000 +
							Integer.parseInt(array[1]) * 1000;
				} else if (array.length == 3) {
					stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 +
							Integer.parseInt(array[1]) * 60 * 1000 +
							Integer.parseInt(array[2]) * 1000;
				}
				
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
				secondsYouWaited = Long.parseLong(array[1]);
				updateTipBasedOnWaited(secondsYouWaited);
				timeWaitingChronometer.start();
			}
		});
		
		pauseChronometerBT.setOnClickListener(new OnClickListener() { 
			
			@Override public void onClick(View v) { 
				timeWaitingChronometer.stop(); 
			} 
		}); 
		
		resetChronometerBT.setOnClickListener(new OnClickListener() { 
			
			@Override public void onClick(View v) { 
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());
				secondsYouWaited = 0; 
			} 
		});
			
	}

	private void updateTipBasedOnWaited(long secondsYouWaited) {
		checkListValues[9] = (secondsYouWaited > 10) ? -2 : 2;
		setTipFromWaitressCheckList();
		updateTipAndFinalBill();
	}
	
	private void addItemSelectedListenerToSpinner() {
		problemSP.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				checkListValues[6] = (problemSP.getSelectedItem()).equals("Bad") ? -1 : 0;
				checkListValues[7] = (problemSP.getSelectedItem()).equals("OK") ? 3 : 0;
				checkListValues[8] = (problemSP.getSelectedItem()).equals("Good") ? 6 : 0;
				
				setTipFromWaitressCheckList();
				updateTipAndFinalBill();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}

	private void addChangeListenerToRadios() {
		availableRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				checkListValues[3] = (availableBadRB.isChecked()) ? -1 : 0;
				checkListValues[4] = (availableOKRB.isChecked()) ? 2 : 0;
				checkListValues[5] = (availableGoodRB.isChecked()) ? 4 : 0;
				
				setTipFromWaitressCheckList();
				updateTipAndFinalBill();
			}
		});
		
	}

	private void specialUpIntroCheckBoxes() {
		friendlyCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkListValues[0] = (isChecked) ? 4 : 0;
				
				setTipFromWaitressCheckList();
				updateTipAndFinalBill();
			}
		});
		
		specialsCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkListValues[1] = (isChecked) ? 1 : 0;
				
				setTipFromWaitressCheckList();
				updateTipAndFinalBill();
			}
		});
		
		opinionCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkListValues[2] = (isChecked) ? 2 : 0;
				
				setTipFromWaitressCheckList();
				updateTipAndFinalBill();
			}
		});
	}

	private void setTipFromWaitressCheckList() {
		int checkListTotal = 0;
		
		for (int item : checkListValues)
			checkListTotal += item;
		
		tipAmountET.setText(String.format("%.2f", checkListTotal * 0.01));
	}	
	
	private void updateTipAndFinalBill() {
		double tipAmount = Double.parseDouble(tipAmountET.getText().toString().trim());
		double finalBill = billBeforeTip + (billBeforeTip * tipAmount);
		
		finalBillET.setText(String.format("%.2f", finalBill));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putDouble(TOTAL_BILL, finalBill);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
