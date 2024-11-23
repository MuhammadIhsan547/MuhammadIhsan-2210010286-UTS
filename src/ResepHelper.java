import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class ResepHelper {
    
    public static Connection KoneksiDatabase() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:E:\\Keluargaku\\Muhammad Ihsan\\Kuliah\\PBO2\\UTS\\DatabaseResepMakanan.db");
            System.out.println("Koneksi berhasil!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver SQLite tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }

    public static void MembuatTabelResep() {
        String sql = "CREATE TABLE IF NOT EXISTS kontak (\n"
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + " nama_resep TEXT NOT NULL,\n"
                    + " bahan_bahan TEXT NOT NULL,\n"
                    + " langkah_langkah TEXT NOT NULL,\n"
                    + " kategori TEXT NOT NULL\n"
                    + ");";
        try (Connection conn = KoneksiDatabase();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void MenambahResep(String nama_resep, String bahanbahan, String langkahlangkah, String kategori) {
        String sql = "INSERT INTO kontak(nama, no_telepon, kategori) VALUES(?, ?, ?)";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama_resep);
            pstmt.setString(2, bahanbahan);
            pstmt.setString(3, langkahlangkah);
            pstmt.setString(4, kategori);
            pstmt.executeUpdate();
            System.out.println("Resep berhasil ditambahkan.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Map<String, String>> DapatkanResep() {
        List<Map<String, String>> kontak = new ArrayList<>();
        String sql = "SELECT * FROM resep";

        try (Connection conn = KoneksiDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, String> dataResep = new HashMap<>();
                dataResep.put("id", String.valueOf(rs.getInt("id")));
                dataResep.put("nama", rs.getString("nama"));
                dataResep.put("no_telepon", rs.getString("no_telepon"));
                dataResep.put("kategori", rs.getString("kategori"));
                kontak.add(dataResep);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return kontak;
    }

    public static void MemperbaruiResep(int id, String nama_resep, String bahanbahan, String langkahlangkah, String kategori) {
        String sql = "UPDATE resep SET nama_resep = ?, bahanbahan = ?, langkahlangkah = ?, kategori = ?, WHERE id = ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama_resep);
            pstmt.setString(2, bahanbahan);
            pstmt.setString(3, langkahlangkah);
            pstmt.setString(4, kategori);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Kontak berhasil diperbarui.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void MenghapusKontak(int id) {
        String sql = "DELETE FROM kontak WHERE id = ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Kontak berhasil dihapus.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static List<Map<String, String>> MencariKontak(String keyword) {
    List<Map<String, String>> ListKontak = new ArrayList<>();
    String sql = "SELECT * FROM kontak WHERE nama LIKE ? OR no_telepon LIKE ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Gunakan '%' untuk pencarian mirip (LIKE)
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, String> kontak = new HashMap<>();
                kontak.put("id", String.valueOf(rs.getInt("id")));
                kontak.put("nama", rs.getString("nama"));
                kontak.put("no_telepon", rs.getString("no_telepon"));
                kontak.put("kategori", rs.getString("kategori"));
                ListKontak.add(kontak);
            }
        } catch (SQLException e) {
            System.out.println("Error saat mencari kontak: " + e.getMessage());
        }
        return ListKontak;
    }

    
    
    
   


    
    
    
    // Mendapatkan resep makanan secara baris per baris
    public void getResepBarisPerBaris(String query, JTextArea txtAreaResep, Supplier<Boolean> shouldStop) {
        
    try {
        String apiKey = "a99ad06c83bc4661a7d508abe178b999";
        String searchUrl = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey + "&query=" + query;
        URL url = new URL(searchUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (shouldStop.get()) {
                in.close();
                conn.disconnect();
                SwingUtilities.invokeLater(() ->
                        txtAreaResep.setText("Pengambilan data dibatalkan.\n"));
                return;
            }
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        JSONObject json = new JSONObject(content.toString());
        JSONArray results = json.getJSONArray("results");

        if (results.length() == 0) {
            SwingUtilities.invokeLater(() ->
                    txtAreaResep.setText("Tidak ada resep yang ditemukan untuk pencarian ini."));
            return;
        }

        for (int i = 0; i < results.length(); i++) {
            if (shouldStop.get()) {
                SwingUtilities.invokeLater(() ->
                        txtAreaResep.setText("Pengambilan data dibatalkan.\n"));
                return;
            }

            JSONObject recipe = results.getJSONObject(i);
            int recipeId = recipe.getInt("id");
            String detailUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=" + apiKey;
            URL detailApiUrl = new URL(detailUrl);
            HttpURLConnection detailConn = (HttpURLConnection) detailApiUrl.openConnection();
            detailConn.setRequestMethod("GET");
            detailConn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int detailResponseCode = detailConn.getResponseCode();
            if (detailResponseCode != 200) {
                throw new Exception("Gagal mendapatkan detail resep dengan ID: " + recipeId);
            }

            BufferedReader detailReader = new BufferedReader(new InputStreamReader(detailConn.getInputStream()));
            StringBuilder detailContent = new StringBuilder();
            while ((inputLine = detailReader.readLine()) != null) {
                if (shouldStop.get()) {
                    detailReader.close();
                    detailConn.disconnect();
                    SwingUtilities.invokeLater(() ->
                            txtAreaResep.setText("Pengambilan data dibatalkan.\n"));
                    return;
                }
                detailContent.append(inputLine);
            }
            detailReader.close();
            detailConn.disconnect();

            JSONObject recipeDetail = new JSONObject(detailContent.toString());
            String title = recipeDetail.getString("title");

            // Terjemahkan judul
            String translatedTitle = translateToIndonesian(title);

            // Ambil bahan-bahan dan terjemahkan
            JSONArray ingredients = recipeDetail.getJSONArray("extendedIngredients");
            StringBuilder bahanBuilder = new StringBuilder();
            for (int j = 0; j < ingredients.length(); j++) {
                JSONObject ingredient = ingredients.getJSONObject(j);
                String bahan = ingredient.getString("original");
                String translatedBahan = translateToIndonesian(bahan);
                bahanBuilder.append("- ").append(translatedBahan).append("\n");
            }

            // Ambil langkah-langkah dan terjemahkan
            JSONArray stepsArray = recipeDetail.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
            StringBuilder langkahBuilder = new StringBuilder();
            for (int k = 0; k < stepsArray.length(); k++) {
                JSONObject step = stepsArray.getJSONObject(k);
                String langkah = step.getString("step");
                String translatedLangkah = translateToIndonesian(langkah);
                langkahBuilder.append(k + 1).append(". ").append(translatedLangkah).append("\n");
            }

            // Tampilkan hasil di JTextArea
            String hasilResep = "Resep: " + translatedTitle + "\n\n"
                    + "Bahan-Bahan:\n" + bahanBuilder.toString() + "\n"
                    + "Langkah-Langkah:\n" + langkahBuilder.toString() + "\n\n";

            SwingUtilities.invokeLater(() ->
                    txtAreaResep.append(hasilResep));
        }
    } catch (Exception e) {
        SwingUtilities.invokeLater(() ->
                txtAreaResep.setText("Gagal mendapatkan data resep: " + e.getMessage()));
    }


    }
    
    

    
    private String translateToEnglish(String keyword) {
    try {
        String urlString = "https://lingva.ml/api/v1/id/en/" + keyword.replace(" ", "%20");
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP response code: " + responseCode);
        }

        StringBuilder content;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        conn.disconnect();

        JSONObject json = new JSONObject(content.toString());
        return json.getString("translation");
    } catch (Exception e) {
        return keyword + " (Gagal diterjemahkan)";
    }
}

    

    
    private String translateToIndonesian(String text) {
    try {
        String urlString = "https://lingva.ml/api/v1/en/id/" + text.replace(" ", "%20");
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP response code: " + responseCode);
        }

        StringBuilder content;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        conn.disconnect();

        System.out.println("Response from API: " + content.toString()); // Debug output

        JSONObject json = new JSONObject(content.toString());
        return json.getString("translation");
    } catch (Exception e) {
        e.printStackTrace(); // Debug error
        return text + " (Gagal diterjemahkan)";
    }
}


}
