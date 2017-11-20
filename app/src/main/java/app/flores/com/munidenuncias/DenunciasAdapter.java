package app.flores.com.munidenuncias;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DenunciasAdapter extends RecyclerView.Adapter<DenunciasAdapter.ViewHolder> {

    private static final String TAG = DenunciasAdapter.class.getSimpleName();

    private List<Denuncia> denuncia;

    public DenunciasAdapter(Activity activity){
        this.denuncia = new ArrayList<>();
    }

    public void setDenuncia(List<Denuncia> denuncias){
        this.denuncia = denuncias;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView fotoImage;
        public TextView titulo;
        public TextView descripcion;
        public TextView ubicacion;


        public ViewHolder(View itemView) {
            super(itemView);
            fotoImage = (ImageView) itemView.findViewById(R.id.foto_image);
            titulo = (TextView) itemView.findViewById(R.id.titulo_text);
           // descripcion = (TextView) itemView.findViewById(R.id.descripcion);
            ubicacion = (TextView) itemView.findViewById(R.id.ubicacion);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_denuncia, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DenunciasAdapter.ViewHolder holder, int position) {

        Denuncia denuncia = this.denuncia.get(position);

        float lat = denuncia.getLat();
        float lng = denuncia.getLng();
        holder.titulo.setText(denuncia.getTitulo());
       /* holder.descripcion.setText(denuncia.getDescripcion());
        holder.ubicacion.setText(denuncia.getUbicacion());
*/
        String url = ApiService.API_BASE_URL + "/images/" + denuncia.getImagen();
        Picasso.with(holder.itemView.getContext()).load(url).into(holder.fotoImage);

    }

    @Override
    public int getItemCount() {
        return this.denuncia.size();
    }

}
