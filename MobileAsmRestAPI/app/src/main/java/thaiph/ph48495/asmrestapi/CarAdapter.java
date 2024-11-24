package thaiph.ph48495.asmrestapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private Context context;
    private List<CarModel> listCar;
    public OnEditClickListener onEditClickListener;
    public OnDetailClickListener onDetailClickListener;
    public OnDeleteClickListener onDeleteClickListener;


    public CarAdapter(Context context, List<CarModel> listCar) {
        this.context = context;
        this.listCar = listCar;
    }

    public interface OnEditClickListener {
        void onEditClick(CarModel carModel);
    }
    public interface OnDetailClickListener {
        void onDetailClick(CarModel carModel);
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(CarModel carModel);
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void setOnDetailClickListener(OnDetailClickListener onDetailClickListener) {
        this.onDetailClickListener = onDetailClickListener;
    }
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.ViewHolder holder, int position) {
        CarModel car = listCar.get(position);
        holder.tvTenXe.setText("Tên xe: "+car.getTenXe());
        holder.tvHangSanXuat.setText("Hãng: "+car.getHangSanXuat());
        holder.tvGiaXe.setText("Giá: "+car.getGiaBan() + " $");

        // Xử lý sự kiện click trên item
        holder.btnEdit.setOnClickListener(v -> {
            if (onEditClickListener != null) {
                onEditClickListener.onEditClick(car);
            }
        });

        // Xử lý sự kiện click trên item
        holder.itemView.setOnClickListener(v -> {
            if (onDetailClickListener != null) {
                onDetailClickListener.onDetailClick(car);
            }
        });

        //Xử lý sự kiện delete
        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(car);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listCar == null) return 0;
        return listCar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenXe, tvGiaXe, tvHangSanXuat;
        ImageView btnEdit, btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenXe = itemView.findViewById(R.id.tvTenXe);
            tvGiaXe = itemView.findViewById(R.id.tvGiaXe);
            tvHangSanXuat = itemView.findViewById(R.id.tvHangSanXuat);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void refreshList(List<CarModel> listCar) {
        this.listCar = listCar;
        notifyDataSetChanged();
    }
}
