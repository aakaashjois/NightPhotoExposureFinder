package biryanistudio.nitefotografr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Aakaash Jois
 * @version 1.2
 * @since 09/04/2016
 */

public class MainActivity extends AppCompatActivity {

	/**
	 * Using Butterknife library to bind UI Elements with Java code
	 */
	@Bind( R.id.spinnerManufacturer )
	Spinner spinnerManufacturer;
	@Bind( R.id.radioSensorType )
	RadioGroup radioSensorType;
	@Bind( R.id.spinnerSensorType )
	Spinner spinnerSensorType;
	@Bind( R.id.textFocalLength )
	EditText textFocalLength;
	@Bind( R.id.textTrail )
	TextView textTrail;
	@Bind( R.id.textStill )
	TextView textStill;
	@Bind( R.id.coordinatorLayout )
	CoordinatorLayout coordinatorLayout;

	/**
	 * variables to check if a Manufacturer and Sensor Type is chosen by the user
	 */
	int chosenManufacturer = -1;
	double sensorTypeValue = -1.0;
	/**
	 * variables isFirstManufacturer and isFirstCrop is used to prevent the app from displaying a
	 * Snackbar on launch.
	 */
	boolean isFirstManufacturer = true;
	boolean isFirstCrop = true;

	/**
	 * Array to hold the different crop sensor values for each manufacturer
	 */
	String[] arraySensorCrop = new String[]{};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initializeUI();
	}

	/**
	 * This method initializes the UI
	 */
	private void initializeUI() {
		spinnerSensorType.setVisibility(View.GONE);
		showManufacturerCard();
		getCropType();
		getFocalLength();
		animateAllCards();
	}

	/**
	 * This method animates the entry of all the cards
	 */
	private void animateAllCards() {
		final LinearLayout linearLayout = ( LinearLayout ) findViewById(R.id.linearLayout);
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		int width = p.x;

		AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
		if ( linearLayout != null ) {
			for ( int i = 0; i < linearLayout.getChildCount(); i++ ) {
				View toAnimateView = linearLayout.getChildAt(i);
				float originalYPos = toAnimateView.getX();
				toAnimateView.setTranslationX(width);
				toAnimateView.animate().translationXBy(originalYPos - width)
						.setInterpolator(interpolator).setStartDelay(i * 100).setDuration(500).start();
			}
		}
	}

	/**
	 * This method displays the manufacturer selection card and prompts the user to select a manufacturer
	 * if none is selected using a Snackbar. Depending on the manufacturer chosen, the spinner for selecting
	 * crop sensor is either enabled or disabled. Some manufacturers only have one crop sensor type and hence
	 * the spinner is unnecessary.
	 */
	private void showManufacturerCard() {
		String[] arrayManufacturer = getResources().getStringArray(R.array.manufacturerList);
		ArrayAdapter<String> adapterManufacturer = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, arrayManufacturer);
		spinnerManufacturer.setAdapter(adapterManufacturer);
		spinnerManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				chosenManufacturer = position;
				switch ( chosenManufacturer ) {
					case 0:
						if ( isFirstManufacturer )
							isFirstManufacturer = false;
						else {
							noManufacturer();
							Snackbar.make(coordinatorLayout, "Please choose a Manufacturer", Snackbar.LENGTH_LONG).show();
						}
						break;
					case 2:
					case 3:
					case 7:
					case 9:
						yesManufacturer();
						spinnerSensorType.setVisibility(View.GONE);
						break;
					case 1:
					case 4:
					case 5:
					case 6:
					case 8:
						yesManufacturer();
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}


	/**
	 * This method loads the appropriate sensor type depending on the choice of the user.
	 * If the user selects a crop sensor, then depending on the manufacturer, the appropriate values
	 * for the crop sensors are loaded into the spinner.
	 */
	private void getCropType() {
		radioSensorType.clearCheck();
		sensorTypeValue = -1;
		radioSensorType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if ( checkedId == R.id.radioFullFrame ) {
					sensorTypeValue = 1.0;
				} else if ( checkedId == R.id.radioCropFrame ) {
					switch ( chosenManufacturer ) {
						case 2:
						case 3:
						case 7:
						case 9:
							sensorTypeValue = 1.5;
							break;
						case 1:
							arraySensorCrop = getResources().getStringArray(R.array.canonCrop);
							getCropFactor();
							break;
						case 4:
							arraySensorCrop = getResources().getStringArray(R.array.leicaCrop);
							getCropFactor();
							break;
						case 5:
							arraySensorCrop = getResources().getStringArray(R.array.nikonCrop);
							getCropFactor();
							break;
						case 6:
							arraySensorCrop = getResources().getStringArray(R.array.pentaxCrop);
							getCropFactor();
							break;
						case 8:
							arraySensorCrop = getResources().getStringArray(R.array.sigmaCrop);
							getCropFactor();
							break;
						default:
							break;
					}
				}
			}
		});

	}

	/**
	 * This methods gets the crop factor from the spinner. If no crop factor is chosen, then a Snackbar
	 * is displayed to prompt the user to select a crop factor.
	 */
	private void getCropFactor() {
		spinnerSensorType.setVisibility(View.VISIBLE);
		ArrayAdapter<String> adapterSensorCrop = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, arraySensorCrop);
		spinnerSensorType.setAdapter(adapterSensorCrop);
		spinnerSensorType.setSelection(0);
		spinnerSensorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			                                            @Override
			                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				                                            if ( position == 0 ) {
					                                            if ( isFirstCrop )
						                                            isFirstCrop = false;
					                                            else {
						                                            Snackbar.make(coordinatorLayout, "Please select the Crop Factor", Snackbar.LENGTH_LONG).show();
						                                            textFocalLength.setEnabled(false);
						                                            textStill.setEnabled(false);
						                                            textTrail.setEnabled(false);
					                                            }
				                                            } else {
					                                            textFocalLength.setEnabled(true);
					                                            textFocalLength.getText().clear();
					                                            textStill.setEnabled(true);
					                                            textTrail.setEnabled(true);
					                                            sensorTypeValue = Double.parseDouble(arraySensorCrop[position]);
				                                            }
			                                            }

			                                            @Override
			                                            public void onNothingSelected(AdapterView<?> parent) {
			                                            }
		                                            }

		);
	}

	/**
	 * This method gets the focal length from the EditText view. The value is continuously retrieved
	 * as the user types and the results are shown immediately.
	 */
	private void getFocalLength() {
		textFocalLength.setEnabled(true);
		textFocalLength.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ( actionId == EditorInfo.IME_ACTION_DONE ) {
					String inputText = textFocalLength.getText().toString();
					if ( !inputText.isEmpty() ) {
						double inputFocalLength = Double.parseDouble(inputText);
						InputMethodManager inputManager = ( InputMethodManager ) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.hideSoftInputFromWindow(textFocalLength.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						getShutterSpeed(inputFocalLength);
					}
					return true;
				}
				return false;
			}
		});
		textFocalLength.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if ( s.length() != 0 )
					getShutterSpeed(Double.parseDouble(s.toString()));
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * This method calculates the shutter speed depending on the sensor type and focal length and displays
	 * it to the user.
	 *
	 * @param inputFocalLength focal length entered by the user. It is the input from the calling method.
	 */
	private void getShutterSpeed(double inputFocalLength) {
		try {
			double shutterSpeed = 500 / (inputFocalLength * sensorTypeValue);
			String trail = getResources().getString(R.string.trail);
			trail = trail + (String.valueOf((( int ) shutterSpeed + 1)));
			trail = trail + getResources().getString(R.string.seconds);
			textTrail.setText(trail);
			String still = getResources().getString(R.string.still);
			still = still + (String.valueOf((( int ) shutterSpeed - 1)));
			still = still + getResources().getString(R.string.seconds);
			textStill.setText(still);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * This method disables the sensor selection card elements if no manufacturer is selected
	 */
	private void noManufacturer() {
		for ( int i = 0; i < radioSensorType.getChildCount(); i++ ) {
			radioSensorType.getChildAt(i).setEnabled(false);
		}
		spinnerSensorType.setVisibility(View.GONE);
		textFocalLength.setEnabled(false);
		textTrail.setEnabled(false);
		textStill.setEnabled(false);

	}

	/**
	 * This method enables the sensor selection card elements when a manufacturer is selected
	 */
	private void yesManufacturer() {

		for ( int i = 0; i < radioSensorType.getChildCount(); i++ ) {
			radioSensorType.getChildAt(i).setEnabled(true);
		}
		radioSensorType.clearCheck();
		textFocalLength.setEnabled(true);
		textTrail.setEnabled(true);
		textStill.setEnabled(true);
	}

	/**
	 * This method is called on the click of the Help buttons. Depending on the button which clicked
	 * for help, the appropriate help is displayed.
	 *
	 * @param view The button view when clicked is passed to this method
	 */
	public void helpDialog(View view) {
		if ( view.getTag() == getString(R.string.helpManufacturer) )
			createDialog(getString(R.string.manufacturer), getString(R.string.manufacturerDesc));
		else if ( view.getTag() == getString(R.string.helpSensor) )
			createDialog(getString(R.string.sensor), getString(R.string.sensorDesc));
		else if ( view.getTag() == getString(R.string.helpFocal) )
			createDialog(getString(R.string.focal), getString(R.string.focalDesc));
		else if ( view.getTag() == getString(R.string.helpShutter) )
			createDialog(getString(R.string.shutter), getString(R.string.shutterDesc));
	}

	/**
	 * This method generates a Dialog which contains the help message
	 *
	 * @param topic The topic of help requested
	 * @param desc  The description on the topic
	 */
	public void createDialog(String topic, String desc) {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle(topic)
				.setMessage(desc)
				.setNeutralButton(R.string.helpDialogOK, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
}
