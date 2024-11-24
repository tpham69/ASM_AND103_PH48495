package thaiph.ph48495.asmrestapi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String TAG = "zzzzzzzzzz";
    private String BASE_URL = "https://labour-fredi-fpt-university-pro-bc6cdffd.koyeb.app/cars/";
    private SearchView searchView;
    private RecyclerView recyclerView;
    CarAdapter carAdapter;
    FloatingActionButton fab;
    List<CarModel> listCar;
    CarService carService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Mapping
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        //Set up RecycleView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carAdapter = new CarAdapter(MainActivity.this, listCar);
        recyclerView.setAdapter(carAdapter);

        //Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        //set up CarService
        carService = retrofit.create(CarService.class);

        //get list car
        getListCar();

        //create car
        createNewCar();

        //detail car on click
        detailCar();

        //edit car
        editCar();

        //delete car
        deleteCar();

        //search car
        searchCar();
    }

    public void searchCar(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<CarModel> filteredList = new ArrayList<>();
                for(CarModel carModel : listCar){
                    if(carModel.getTenXe().toLowerCase().contains(query.toLowerCase()) || carModel.getHangSanXuat().toLowerCase().contains(query.toLowerCase())){
                        filteredList.add(carModel);
                    }
                }
                carAdapter.refreshList(filteredList);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<CarModel> filteredList = new ArrayList<>();
                for(CarModel carModel : listCar){
                    if(carModel.getTenXe().toLowerCase().contains(newText.toLowerCase()) || carModel.getHangSanXuat().toLowerCase().contains(newText.toLowerCase())){
                        filteredList.add(carModel);
                    }
                }
                carAdapter.refreshList(filteredList);
                return true;
            }
        });
    }

    public void deleteCar(){
        carAdapter.setOnDeleteClickListener(carModel -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có chắc chắn muốn xóa xe này không?");
            builder.setPositiveButton("Có", (dialog, which) -> {
                carService.deleteCar(carModel.get_id()).enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                            getListCar();
                        } else {
                            Toast.makeText(MainActivity.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {

                    }
                });
            });
            builder.setNegativeButton("Không", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();
        });
    }

    public void detailCar(){
        carAdapter.setOnDetailClickListener(carModel -> {
            Log.d(TAG, "detailCar: clicked!");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_chitiet_car, null);
            builder.setView(view);

            //Mapping
            TextView tvID = view.findViewById(R.id.tvID);
            TextView tvTenXe = view.findViewById(R.id.tvTenXe);
            TextView tvHangSanXuat = view.findViewById(R.id.tvHangSanXuat);
            TextView tvNamSanXuat = view.findViewById(R.id.tvNamSanXuat);
            TextView tvGiaBan = view.findViewById(R.id.tvGiaBan);
            TextView tvMoTa = view.findViewById(R.id.tvMoTa);

            AlertDialog dialog = builder.create();

            tvID.setText("ID: "+carModel.get_id());
            tvTenXe.setText("Tên xe: "+carModel.getTenXe());
            tvHangSanXuat.setText("Hãng sản xuất: "+carModel.getHangSanXuat());
            tvNamSanXuat.setText("Năm sản xuất: "+carModel.getNamSanXuat());
            tvGiaBan.setText("Giá bán: "+carModel.getGiaBan()+" $");
            tvMoTa.setText("Mô tả: "+carModel.getMoTa());

            dialog.show();
        });
    }

    public void editCar(){
        carAdapter.setOnEditClickListener(carModel -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_add_car, null);
            builder.setView(view);

            //Mapping
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            EditText edtTenXe = view.findViewById(R.id.edtTenXe);
            EditText edtHangSanXuat = view.findViewById(R.id.edtHangSanXuat);
            EditText edtNamSanXuat = view.findViewById(R.id.edtNamSanXuat);
            EditText edtGiaBan = view.findViewById(R.id.edtGiaBan);
            EditText edtMoTa = view.findViewById(R.id.edtMoTa);
            Button btnLuu = view.findViewById(R.id.btnLuu);

            AlertDialog dialog = builder.create();

            //Set data
            tvTitle.setText("Sửa thông tin xe");
            edtTenXe.setText(carModel.getTenXe());
            edtHangSanXuat.setText(carModel.getHangSanXuat());
            edtNamSanXuat.setText(String.valueOf(carModel.getNamSanXuat()));
            edtGiaBan.setText(String.valueOf(carModel.getGiaBan()));
            edtMoTa.setText(carModel.getMoTa());
            btnLuu.setText("LƯU");

            btnLuu.setOnClickListener(v1 ->{
                String id = carModel.get_id();
                String tenXe = edtTenXe.getText().toString();
                String hangSanXuat = edtHangSanXuat.getText().toString();
                String namSanXuat = edtNamSanXuat.getText().toString();
                String giaBan = edtGiaBan.getText().toString();
                String moTa = edtMoTa.getText().toString();

                if(tenXe.isEmpty() || hangSanXuat.isEmpty() || namSanXuat.isEmpty() || giaBan.isEmpty() || moTa.isEmpty()){
                    Toast.makeText(this, "Không thể trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Integer.parseInt(namSanXuat) < 1900 || Integer.parseInt(namSanXuat) > 2025){
                    Toast.makeText(this, "Năm sản xuất không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Integer.parseInt(giaBan) < 0){
                    Toast.makeText(this, "Giá bán không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                CarModel updatedCar = new CarModel(id, tenXe, hangSanXuat, Integer.parseInt(namSanXuat), Integer.parseInt(giaBan), moTa);

                carService.updateCar(id,updatedCar).enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Sửa thành công!", Toast.LENGTH_SHORT).show();
                            getListCar();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Sửa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {

                    }
                });
            });


            dialog.show();
        });
    }

    public void createNewCar(){
        fab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_add_car, null);
            builder.setView(view);

            //Mapping
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            EditText edtTenXe = view.findViewById(R.id.edtTenXe);
            EditText edtHangSanXuat = view.findViewById(R.id.edtHangSanXuat);
            EditText edtNamSanXuat = view.findViewById(R.id.edtNamSanXuat);
            EditText edtGiaBan = view.findViewById(R.id.edtGiaBan);
            EditText edtMoTa = view.findViewById(R.id.edtMoTa);
            Button btnLuu = view.findViewById(R.id.btnLuu);

            AlertDialog dialog = builder.create();

            btnLuu.setOnClickListener(v1 ->{
                String tenXe = edtTenXe.getText().toString();
                String hangSanXuat = edtHangSanXuat.getText().toString();
                String namSanXuat = edtNamSanXuat.getText().toString();
                String giaBan = edtGiaBan.getText().toString();
                String moTa = edtMoTa.getText().toString();

                if(tenXe.isEmpty() || hangSanXuat.isEmpty() || namSanXuat.isEmpty() || giaBan.isEmpty() || moTa.isEmpty()){
                    Toast.makeText(this, "Không thể trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Integer.parseInt(namSanXuat) < 1900 || Integer.parseInt(namSanXuat) > 2025){
                    Toast.makeText(this, "Năm sản xuất không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Integer.parseInt(giaBan) < 0){
                    Toast.makeText(this, "Giá bán không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                CarModel carModel = new CarModel(null, tenXe, hangSanXuat, Integer.parseInt(namSanXuat), Integer.parseInt(giaBan), moTa);

                carService.createCar(carModel).enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                            getListCar();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {
                        Log.d(TAG, "Lỗi gọi API: " + t.getMessage());
                    }
                });
            });

            dialog.show();
        });
    }

    public void getListCar(){
        // Hiển thị loading spinner
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        carService.getAllCar().enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                progressBar.setVisibility(View.GONE);
                listCar = response.body();
                Log.d(TAG, "onResponse: listCar: " + listCar.size());
                carAdapter.refreshList(listCar);
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.d(TAG, "Lỗi gọi API: " + t.getMessage());
            }
        });
    }
}