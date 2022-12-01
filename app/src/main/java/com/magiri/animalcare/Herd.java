package com.magiri.animalcare;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.magiri.animalcare.Adapters.AnimalAdapter;
import com.magiri.animalcare.Model.Animal;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.UI.DropDown;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Herd extends AppCompatActivity {
    private static final String TAG = "Herd";
    private MaterialToolbar herdMaterialToolBar;
    private FloatingActionButton addAnimalFAB;
    private RecyclerView animalRecyclerView;
    private AnimalAdapter animalAdapter;
    List<Animal> animalList;
    StorageTask storageTask;
    private DatabaseReference mRef,databaseReference;
    String FarmerID;
    String[] breed,group,status;
    ProgressDialog progressDialog,mProgressDialog;
    AlertDialog alertDialog;
    private static final int image_Pick_Code=2;
    private static final int IMAGE_REQUEST_CODE=123;
    private Uri imageViewUri=null;
    ImageButton animalImageBtn;
    StorageReference storageReference;
    ArrayAdapter groupsAdapter,statusAdapter,breedAdapter;
    String selectedStatus;
    String selectedBreed;
    String animalGroup;
    boolean animalExists;
    LinearLayout filterLayout;
    RadioButton pregnantRadioBtn,dryRadioBtn,lactatingRadioBtn;
    RadioGroup animalStatusRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herd);

        herdMaterialToolBar=findViewById(R.id.herdMaterialToolBar);
        addAnimalFAB=findViewById(R.id.addAnimalFloatingBar);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        setSupportActionBar(herdMaterialToolBar);
        animalList=new ArrayList<>();
        animalRecyclerView=findViewById(R.id.herdRecyclerView);
        animalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        animalRecyclerView.setHasFixedSize(true);
        mRef= FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID);
        databaseReference=FirebaseDatabase.getInstance().getReference("Animal");
        breed=getResources().getStringArray(R.array.breed);
        status=getResources().getStringArray(R.array.animal_status);
        group=getResources().getStringArray(R.array.age_group);
        storageReference= FirebaseStorage.getInstance().getReference();
        filterLayout=findViewById(R.id.filterLayout);
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Filtering Animals");

        pregnantRadioBtn=findViewById(R.id.pregnantRadioBtn);
        lactatingRadioBtn=findViewById(R.id.lactatingRadioBtn);
        dryRadioBtn=findViewById(R.id.dryRadioBtn);
        animalStatusRadioGroup=findViewById(R.id.filterLayout);

        animalStatusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.pregnantRadioBtn:
                        filterAnimalByStatus("Pregnant");
                        break;
                    case R.id.dryRadioBtn:
                        filterAnimalByStatus("Dry");
                        break;
                    case R.id.lactatingRadioBtn:
                        filterAnimalByStatus("Lactating");
                        break;
                }
            }
        });


        groupsAdapter=new ArrayAdapter(Herd.this,R.layout.item,getResources().getStringArray(R.array.age_group));
        breedAdapter=new ArrayAdapter(Herd.this,R.layout.item,getResources().getStringArray(R.array.breed));
        statusAdapter=new ArrayAdapter(Herd.this,R.layout.item,getResources().getStringArray(R.array.animal_status));
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Adding Animal");
        progressDialog.setCanceledOnTouchOutside(false);


        getMyHerd();


        herdMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addAnimalFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDailogBuilder=new AlertDialog.Builder(Herd.this);
                LayoutInflater layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view=layoutInflater.inflate(R.layout.add_animal_layout,null);
                alertDailogBuilder.setView(view);
                alertDailogBuilder.setCancelable(false);
                TextInputEditText animalNameTxt=view.findViewById(R.id.animalNameTxt);
                TextInputEditText dateText=view.findViewById(R.id.dobText);
                TextInputLayout statusLayout=view.findViewById(R.id.statusLayout);
                AutoCompleteTextView groupDropDown=view.findViewById(R.id.animalGroup);
                AutoCompleteTextView breedDropDown=view.findViewById(R.id.breedSpinner);
                AutoCompleteTextView statusDropDown=view.findViewById(R.id.statusDropDown);
                ImageView closeAlertDialogImageView=view.findViewById(R.id.closeBtn);
                Button addBtn=view.findViewById(R.id.addAnimalBtn);

                closeAlertDialogImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                animalImageBtn=view.findViewById(R.id.animalImageUpload);
                animalImageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkReadPermission();
                    }
                });
                dateText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
                        builder.setTitleText("Animal Date of Birth");
                        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
                        builder.setCalendarConstraints(calendarConstraintBuilder.build());

                        final MaterialDatePicker<Long> materialDatePicker = builder.build();
                        materialDatePicker.show(getSupportFragmentManager(),materialDatePicker.toString());
                        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                calendar.setTimeInMillis(selection);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                dateText.setText(sdf.format(calendar.getTime()));
                            }
                        });
                    }
                });
                groupDropDown.setAdapter(groupsAdapter);
                breedDropDown.setAdapter(breedAdapter);
                statusDropDown.setAdapter(statusAdapter);

                groupDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        animalGroup=parent.getItemAtPosition(position).toString();
                        if(animalGroup.equals("Above 18 months")){
                            statusLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
                breedDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedBreed=parent.getItemAtPosition(position).toString();
                    }
                });
                statusDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedStatus=parent.getItemAtPosition(position).toString();
                    }
                });

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String DOB=dateText.getText().toString();
                        String animalName=animalNameTxt.getText().toString();
                        if(TextUtils.isEmpty(animalName)){
                            animalNameTxt.setError("Required");
                            return;
                        }
                        if(TextUtils.isEmpty(selectedBreed)){
                            breedDropDown.setError("Required");
                            return;
                        }
                        if(TextUtils.isEmpty(DOB)){
                            dateText.setError("Date Required");
                            return;
                        }if(TextUtils.isEmpty(animalGroup)){
                            groupDropDown.setError("Required");
                            return;
                        }
                        if(TextUtils.isEmpty(selectedStatus) && !TextUtils.isEmpty(animalGroup)){
                            selectedStatus="Heifer";
                        }
                        if(imageViewUri==null){
                            Toast.makeText(Herd.this,"Please Upload Image",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(AnimalExists(animalName)){
                            Toast.makeText(Herd.this,"Animal already Exists",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        progressDialog.show();
                        addNewAnimal(animalName,selectedBreed,DOB,selectedStatus,imageViewUri);
                    }
                });
                alertDialog=alertDailogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.show();
            }
        });
    }

    private void filterAnimalByStatus(String Status) {
        mProgressDialog.show();
        mRef.orderByChild("status").equalTo(Status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Animal myAnimal=dataSnapshot.getValue(Animal.class);
                    if(myAnimal.getOwnerID().equals(FarmerID)){
                        animalList.add(myAnimal);
                        animalAdapter=new AnimalAdapter(Herd.this,animalList);
                        animalRecyclerView.setAdapter(animalAdapter);
                    }
                }
                mProgressDialog.dismiss();
                animalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Herd.this, "Something wrong Happened", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private boolean AnimalExists(String animalName) {
        animalExists=false;
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(animalName)){
                    animalExists=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
        return animalExists;
    }

    private void checkReadPermission() {
        if(ContextCompat.checkSelfPermission(Herd.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            selectAnimalImage();
        }else{
            ActivityCompat.requestPermissions(Herd.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},IMAGE_REQUEST_CODE);
        }
    }

    private void selectAnimalImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Photo to Post"),image_Pick_Code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==IMAGE_REQUEST_CODE && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectAnimalImage();
        }else{
            showAlertWarning();
        }
    }

    private void showAlertWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AnimalCare App Require Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            openSettings();
            dialog.cancel();
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == image_Pick_Code && resultCode== RESULT_OK && data !=null){
            imageViewUri=data.getData();
            try{
                if(imageViewUri!=null){
                    Bitmap postImageBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageViewUri);
                    animalImageBtn.setImageBitmap(postImageBitmap);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void addNewAnimal(String animalName, String breed, String dob, String status, Uri animalImageUrl) {
        final StorageReference storageRef;
        storageRef=storageReference.child("AnimalImages").child(System.currentTimeMillis()+"."+getFileExtension(animalImageUrl));
        storageTask=storageRef.putFile(animalImageUrl);
        storageTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isComplete()){
                    throw task.getException();
                }
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isComplete()){
                    Uri downloadUri= (Uri) task.getResult();
                    String animalImageUrl=downloadUri.toString();
                    String pushID=mRef.push().getKey();
                    Animal animal=new Animal(animalName,breed,dob,FarmerID,pushID,status,animalImageUrl);
                    mRef.child(animalName).setValue(animal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                alertDialog.dismiss();
                                Toast.makeText(Herd.this,"Animal was added Successfully",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                            Toast.makeText(Herd.this,"Animal was Not Added",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else{
                    Toast.makeText(Herd.this,"Something wrong Happened",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getMyHerd() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Animal myAnimal=dataSnapshot.getValue(Animal.class);
                    if(myAnimal.getOwnerID().equals(FarmerID)){
                        animalList.add(myAnimal);
                        animalAdapter=new AnimalAdapter(Herd.this,animalList);
                        animalRecyclerView.setAdapter(animalAdapter);
                    }
                }
                animalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Herd.this, "Something wrong Happened", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
    private String getFileExtension(Uri imageViewUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageViewUri));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.animal_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.app_bar_search);

        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Animal");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                animalAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.filter:
                if(filterLayout.getVisibility()==View.VISIBLE){
                    filterLayout.setVisibility(View.GONE);
                }else {
                    filterLayout.setVisibility(View.VISIBLE);
                    getMyHerd();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}