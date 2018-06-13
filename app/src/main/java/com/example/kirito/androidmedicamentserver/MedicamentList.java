package com.example.kirito.androidmedicamentserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kirito.androidmedicamentserver.Common.Common;
import com.example.kirito.androidmedicamentserver.Interface.ItemClickListener;
import com.example.kirito.androidmedicamentserver.Model.Category;
import com.example.kirito.androidmedicamentserver.Model.Medicament;
import com.example.kirito.androidmedicamentserver.ViewHolder.MedicamentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class MedicamentList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;


    //Boton que esta al aire para adicionar un nuevo elemento
    FloatingActionButton fab;

    //FIREBASE
    FirebaseDatabase db;
    DatabaseReference medicamentList;

    //FIREBASE almacenamiento de las imagenes
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId = "";

    FirebaseRecyclerAdapter<Medicament, MedicamentViewHolder> adapter;

    //Addicioamos el nuevo medicamento
    MaterialEditText edtName, edtDescription, edtPrice, edtDiscount;
    FButton btnSelect, btnUpLoad;

    Medicament newMedicament;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicament_list);

        //Firebase inicializacion
        db = FirebaseDatabase.getInstance();
        medicamentList = db.getReference("Medicament");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Inicamos al recycler view
        recyclerView =(RecyclerView)findViewById(R.id.recycler_medicament);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMedicamentDialog();
            }
        });

        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty())
            loadListMedicament(categoryId);
    }

    private void showAddMedicamentDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MedicamentList.this);
        alertDialog.setTitle("Agregar nuevo Medicamento");
        alertDialog.setMessage("Por favor llene los datos");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_medicament_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpLoad = add_menu_layout.findViewById(R.id.btnUpLoad);

        //Evento para el boton de carga de imagen
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //El usuario selecciona la imagen desde su galeria y guarda la image por medio de Uri
            }
        });

        btnUpLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Ponemos un boton
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                //Aqui es donde creamos la nueva categoria de medicamentos
                if(newMedicament != null){
                    medicamentList.push().setValue(newMedicament);
                    Snackbar.make(rootLayout, "Nueva Categoria "+newMedicament.getName()+ " Fue adicionada", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void uploadImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Cargando...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(MedicamentList.this, "Cargado!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Ingresamos la nueva Categoria si la imagen descargada y podemos descargar el link
                                    newMedicament = new Medicament();
                                    newMedicament.setName(edtName.getText().toString());
                                    newMedicament.setDescription(edtDescription.getText().toString());
                                    newMedicament.setPrice(edtPrice.getText().toString());
                                    newMedicament.setDiscount(edtDiscount.getText().toString());
                                    newMedicament.setMenuId(categoryId);
                                    newMedicament.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override

                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(MedicamentList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //No nos preocupemos por este error
                            double progress =(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Cargando" + progress+"%");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona la Imagen"), Common.PICK_IMAGE_REQUEST);
    }

    private void loadListMedicament(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Medicament, MedicamentViewHolder>(
                Medicament.class,
                R.layout.medicament_item,
                MedicamentViewHolder.class,
                medicamentList.orderByChild("menuId") .equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(MedicamentViewHolder viewHolder, Medicament model, int position) {
                viewHolder.medicament_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.medicament_image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean inLongClick) {
                        //Codificamos luego
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            saveUri = data.getData();
            btnSelect.setText("Imagen Seleccionada..!");
        }
    }
}
