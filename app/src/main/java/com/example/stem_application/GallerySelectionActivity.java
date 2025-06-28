package com.example.stem_application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GallerySelectionActivity extends AppCompatActivity {

    private GridView butterflyGrid;
    private List<Butterfly> butterflies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleryselection);

        butterflyGrid = findViewById(R.id.butterflyGrid);

        // Initialize butterfly data
        butterflies = new ArrayList<>();
        butterflies.add(new Butterfly("Monarch", R.drawable.monarch, "The monarch butterfly is a famous butterfly with bright orange-red wings, black stripes, and white spots. Its wings can stretch up to 4 inches (10 cm) wide. The bright colors warn predators that the butterfly is poisonous. Monarchs live in North, Central, and South America."));
        butterflies.add(new Butterfly("Black Swallowtail", R.drawable.blackswallowtail, "The black swallowtail butterfly has black wings with yellow spots and shiny blue patches. Like all swallowtails, it has long 'tails' on its wings."));
        butterflies.add(new Butterfly("Blue Morpho", R.drawable.bluemorpho, "The blue morpho is a huge, beautiful butterfly that lives in the rainforest. Its wings shine bright blue and can grow as big as 8 inches—that’s one of the biggest butterflies in the world!"));
        butterflies.add(new Butterfly("Crimson Rose", R.drawable.crimsonrose, "The crimson rose is a pretty butterfly from Asia. It has soft black wings with bright red spots and a shiny red body. Its wings can grow as big as your hand!"));
        butterflies.add(new Butterfly("Gulf Fritillary", R.drawable.gulffritillary, "The Gulf Fritillary is a pretty orange butterfly with black spots. When it closes its wings, you can see shiny silver patterns underneath!"));
        butterflies.add(new Butterfly("Malachite", R.drawable.malachite, "The Malachite butterfly is a big, beautiful butterfly with black and bright green wings that look like shiny gemstones! It lives in rainforests from Texas all the way to Brazil."));

        ButterflyAdapter adapter = new ButterflyAdapter(this, butterflies);
        butterflyGrid.setAdapter(adapter);

        butterflyGrid.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(GallerySelectionActivity.this, PictureViewActivity.class);
            intent.putExtra("butterfly", butterflies.get(position));
            startActivity(intent);
        });
    }

    private static class ButterflyAdapter extends BaseAdapter {
        private Context context;
        private List<Butterfly> butterflies;

        public ButterflyAdapter(Context context, List<Butterfly> butterflies) {
            this.context = context;
            this.butterflies = butterflies;
        }

        @Override
        public int getCount() {
            return butterflies.size();
        }

        @Override
        public Object getItem(int position) {
            return butterflies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_butterfly_card, parent, false);
            }

            ImageView image = convertView.findViewById(R.id.butterflyImage);
            TextView name = convertView.findViewById(R.id.butterflyName);

            Butterfly butterfly = butterflies.get(position);
            image.setImageResource(butterfly.getImageResId());
            name.setText(butterfly.getName());

            return convertView;
        }
    }
}