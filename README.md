# AplikasiResepMasakan
 UTS - Muhammad Ihsan - 2210010286

## Deskripsi Program

Aplikasi Resep Makanan adalah aplikasi berbasis Java dengan antarmuka GUI sederhana untuk mengelola data resep makanan. Aplikasi ini memungkinkan pengguna untuk menambahkan, mengedit, mencari, menghapus, mengimpor, dan mengekspor resep makanan. Selain itu, aplikasi dapat menampilkan resep makanan dari API eksternal.

---

## Fitur Utama

1. **Manajemen Resep**  
   - Tambah, ubah, hapus resep makanan.  
   - Data resep meliputi nama resep, bahan-bahan, langkah-langkah, dan kategori.  

2. **Kategori Pengolahan Resep**  
   - Pengguna dapat mengelompokkan resep berdasarkan kategori pengolahan, seperti *goreng*, *rebus*, *panggang*, dll.

3. **Pencarian Resep**  
   - Fitur pencarian lokal untuk mencari resep berdasarkan nama.

4. **Ekspor dan Impor Resep**  
   - Impor resep dari file CSV.  
   - Ekspor resep ke file CSV.  

5. **Integrasi API Eksternal**  
   - Menampilkan resep dari API Spoonacular.  

6. **Penerjemahan Konten**  
   - Data dari API diterjemahkan secara otomatis ke dalam bahasa Indonesia.

---

## Struktur Program

### Kelas Utama

1. **AplikasiResepMakanan**  
   Mengelola antarmuka pengguna dan berbagai aksi seperti menambah, menghapus, mengedit, mencari, mengimpor, dan mengekspor resep.

2. **ResepHelper**  
   Mengelola koneksi ke database SQLite dan mengimplementasikan fungsi CRUD, pencarian, serta integrasi API.

---

## Cara Penggunaan

1. Jalankan program **AplikasiResepMakanan**.
2. Gunakan antarmuka untuk:
   - Menambah resep dengan mengisi nama, bahan-bahan, langkah-langkah, dan kategori.
   - Mengimpor resep dari file CSV.
   - Mengekspor resep ke file CSV.
   - Melakukan pencarian resep berdasarkan nama.
   - Menampilkan resep dari API eksternal dengan mengetikkan kata kunci di bagian "Cari Resep dari Internet".

---


##  Kode Terkait

### 1. **Manajemen Resep**  

#### a. Tambah Resep  
Fitur ini memungkinkan pengguna untuk menambahkan resep baru dengan mengisi data seperti nama resep, bahan-bahan, langkah-langkah, dan kategori.  

**Kode GUI:**
```java
private void TombolTambahResepActionPerformed(java.awt.event.ActionEvent evt) {
    String nama = FieldMasukkanResep.getText();
    String bahan_bahan = jTextAreaBahanBahan.getText();
    String langkah_langkah = jTextAreaLangkahLangkah.getText();
    String kategori = ComboBoxKategori.getSelectedItem().toString();

    if (nama.isEmpty() || bahan_bahan.isEmpty() || langkah_langkah.isEmpty() || kategori.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua data harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    ResepHelper.MenambahResep(nama, bahan_bahan, langkah_langkah, kategori);
    FieldMasukkanResep.setText("");
    jTextAreaBahanBahan.setText("");
    jTextAreaLangkahLangkah.setText("");
    ComboBoxKategori.setSelectedIndex(0);
    MenampilkanResep();
}
```

**Kode Helper:**
```java
public static void MenambahResep(String nama_resep, String bahan_bahan, String langkah_langkah, String kategori) {
    String sql = "INSERT INTO resep(nama_resep, bahan_bahan, langkah_langkah, kategori) VALUES(?, ?, ?, ?)";
    try (Connection conn = KoneksiDatabase();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nama_resep);
        pstmt.setString(2, bahan_bahan);
        pstmt.setString(3, langkah_langkah);
        pstmt.setString(4, kategori);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

---

#### b. Edit Resep  
Mengubah data resep yang sudah ada.  

**Kode GUI:**
```java
private void TombolEditResepActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = TabelDaftarResep.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih Resep Yang Ingin Diedit", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int id = Integer.parseInt(TabelDaftarResep.getValueAt(selectedRow, 0).toString());
    String nama = FieldMasukkanResep.getText();
    String bahan_bahan = jTextAreaBahanBahan.getText();
    String langkah_langkah = jTextAreaLangkahLangkah.getText();
    String kategori = ComboBoxKategori.getSelectedItem().toString();

    ResepHelper.MemperbaruiResep(id, nama, bahan_bahan, langkah_langkah, kategori);
    MenampilkanResep();
}
```

**Kode Helper:**
```java
public static void MemperbaruiResep(int id, String nama_resep, String bahan_bahan, String langkah_langkah, String kategori) {
    String sql = "UPDATE resep SET nama_resep = ?, bahan_bahan = ?, langkah_langkah = ?, kategori = ? WHERE id = ?";
    try (Connection conn = KoneksiDatabase();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nama_resep);
        pstmt.setString(2, bahan_bahan);
        pstmt.setString(3, langkah_langkah);
        pstmt.setString(4, kategori);
        pstmt.setInt(5, id);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

---

#### c. Hapus Resep  
Menghapus resep dari database.  

**Kode GUI:**
```java
private void TombolHapusResepActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = TabelDaftarResep.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih Resep Yang Ingin Dihapus", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int id = Integer.parseInt(TabelDaftarResep.getValueAt(selectedRow, 0).toString());
    ResepHelper.MenghapusResep(id);
    MenampilkanResep();
}
```

**Kode Helper:**
```java
public static void MenghapusResep(int id) {
    String sql = "DELETE FROM resep WHERE id = ?";
    try (Connection conn = KoneksiDatabase();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}
```

---

### 2. **Pencarian Resep**  

Pencarian dilakukan berdasarkan nama resep menggunakan fitur pencarian lokal.  

**Kode GUI:**
```java
private void TombolCariResepDataActionPerformed(java.awt.event.ActionEvent evt) {
    String keyword = FieldCariResep.getText().trim();
    List<Map<String, String>> hasilPencarian = ResepHelper.MencariResep(keyword);
    DefaultTableModel model = (DefaultTableModel) TabelDaftarResep.getModel();
    model.setRowCount(0);

    for (Map<String, String> data : hasilPencarian) {
        model.addRow(new Object[]{
            data.get("id"),
            data.get("nama_resep"),
            data.get("bahan_bahan"),
            data.get("langkah_langkah"),
            data.get("kategori")
        });
    }
}
```

**Kode Helper:**
```java
public static List<Map<String, String>> MencariResep(String keyword) {
    List<Map<String, String>> ListResep = new ArrayList<>();
    String sql = "SELECT * FROM resep WHERE nama_resep LIKE ?";
    try (Connection conn = KoneksiDatabase();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + keyword + "%");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Map<String, String> resep = new HashMap<>();
            resep.put("id", String.valueOf(rs.getInt("id")));
            resep.put("nama_resep", rs.getString("nama_resep"));
            resep.put("bahan_bahan", rs.getString("bahan_bahan"));
            resep.put("langkah_langkah", rs.getString("langkah_langkah"));
            resep.put("kategori", rs.getString("kategori"));
            ListResep.add(resep);
        }
    } catch (SQLException e) {
        System.out.println("Error saat mencari resep: " + e.getMessage());
    }
    return ListResep;
}
```

---

### 3. **Ekspor dan Impor Resep**

#### a. Ekspor Resep ke CSV  
Data resep yang ada di tabel dapat diekspor ke file CSV.

**Kode:**
```java
private void TombolSimpanActionPerformed(java.awt.event.ActionEvent evt) {
    try (FileWriter csvWriter = new FileWriter("Daftar_Resep.csv")) {
        csvWriter.append("ID;Nama Resep;Bahan-Bahan;Langkah-Langkah;Kategori\n");
        DefaultTableModel model = (DefaultTableModel) TabelDaftarResep.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                csvWriter.append(model.getValueAt(i, j).toString()).append(";");
            }
            csvWriter.append("\n");
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saat mengekspor data.");
    }
}
```

#### b. Impor Resep dari CSV  
Mengimpor data dari file CSV ke database.

**Kode:**
```java
private void TombolMuatActionPerformed(java.awt.event.ActionEvent evt) {
    JFileChooser fileChooser = new JFileChooser();
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // Lewati header
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                ResepHelper.MenambahResep(values[1], values[2], values[3], values[4]);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengimpor data.");
        }
    }
}
```

---

### 4. **Integrasi API Eksternal**  

Aplikasi mengambil data resep dari API Spoonacular berdasarkan kata kunci yang dimasukkan pengguna.  

**Kode GUI:**
```java


private void TombolCariInternetActionPerformed(java.awt.event.ActionEvent evt) {
    String query = FieldResepInternet.getText().trim();
    if (!query.isEmpty()) {
        ResepHelper helper = new ResepHelper();
        helper.getResepBarisPerBaris(query, txtAreaResep, () -> stopFetching);
    }
}
```
**Kode Helper:**
```java
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
```

---
## Tampilan Pada Saat Aplikasi Di Jalankan

![UTS](https://github.com/user-attachments/assets/831acf8d-046d-49fa-88e3-8d8141e2fbc8)


---

## Mekanisme Penilaian UTS

| **No** | **Komponen**                      | **Persentase** |
|--------|-----------------------------------|----------------|
| 1      | Fungsional aplikasi               | 20%            |
| 2      | Desain dan pengalaman pengguna    | 20%            |
| 3      | Penerapan konsep OOP              | 15%            |
| 4      | Kreativitas dan inovasi fitur     | 15%            |
| 5      | Dokumentasi kode                  | 10%            |
| **6**  | **Tantangan (impor/ekspor data)** | **20%**        |

**Total:** 100%
---
## Pembuat

- **Nama:** Muhammad Ihsan  
- **NPM:** 2210010286  
- **Kelas:** 5A Ti Reg Pagi BJM  

--- 


