package br.com.fiap.dao;

import br.com.fiap.exception.NotFoundException;
import br.com.fiap.factory.ConnectionFactory;
import br.com.fiap.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() throws SQLException {
        this.connection = ConnectionFactory.getConnection();
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public Product findById(Long id) throws SQLException, NotFoundException {
        PreparedStatement stm = connection.prepareStatement("SELECT * FROM tb_products WHERE cd_product = ?");
        stm.setLong(1, id);
        ResultSet rs = stm.executeQuery();
        if (!rs.next()) {
            throw new NotFoundException(String.format("Product with id %s could not be found!", id));
        }
        Long productId = rs.getLong("cd_product");
        String name = rs.getString("nm_product");
        String description = rs.getString("ds_product");
        double price = rs.getDouble("vl_product");
        int quantity = rs.getInt("nr_stock");

        return new Product(productId, name, description, price, quantity);
    }

    public List<Product> findByName(String productName) throws SQLException {
        PreparedStatement stm = connection.prepareStatement("SELECT * FROM tb_products WHERE nm_product like ?");
        stm.setString(1, "%"+productName+"%");
        ResultSet rs = stm.executeQuery();
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            Long id = rs.getLong("cd_product");
            String name = rs.getString("nm_product");
            String description = rs.getString("ds_product");
            double price = rs.getDouble("vl_product");
            int quantity = rs.getInt("nr_stock");
            products.add(new Product(id, name, description, price, quantity));
        }
        return products;
    }

    public List<Product> getAll() throws SQLException {
        PreparedStatement stm = connection.prepareStatement("SELECT * FROM tb_products");
        ResultSet rs = stm.executeQuery();
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            Long productId = rs.getLong("cd_product");
            String name = rs.getString("nm_product");
            String description = rs.getString("ds_product");
            double price = rs.getDouble("vl_product");
            int quantity = rs.getInt("nr_stock");
            products.add(new Product(productId, name, description, price, quantity));
        }
        return products;
    }

    public void insert(Product product) throws SQLException {
        PreparedStatement stm = connection.prepareStatement("INSERT INTO tb_products(cd_product, nm_product,ds_product,vl_product,nr_stock) VALUES(seq_product.nextval,?,?,?,?)", new String[]{"cd_product"});
        stm.setString(1, product.getName());
        stm.setString(2, product.getDescription());
        stm.setDouble(3, product.getPrice());
        stm.setInt(4, product.getStock());

        stm.executeUpdate();

        ResultSet generatedKeys = stm.getGeneratedKeys();
        if (generatedKeys.next()) {
            product.setId(generatedKeys.getLong(1));
        }
    }

    public void update(Product product) throws SQLException, NotFoundException {
        PreparedStatement stm = connection.prepareStatement("UPDATE tb_products SET nm_product = ?, ds_product = ?, vl_product = ?, nr_stock = ? WHERE cd_product = ?");
        stm.setString(1, product.getName());
        stm.setString(2, product.getDescription());
        stm.setDouble(3, product.getPrice());
        stm.setInt(4, product.getStock());
        stm.setLong(5, product.getId());
        int line = stm.executeUpdate();

        if (line == 0) {
            throw new NotFoundException(String.format("Product with id %s could not be found!", product.getId()));
        }
    }

    public void delete(long id) throws SQLException, NotFoundException {
        PreparedStatement stm = connection.prepareStatement("DELETE FROM tb_products WHERE cd_product = ?");
        stm.setLong(1, id);
        int line = stm.executeUpdate();

        if (line == 0) {
            throw new NotFoundException(String.format("Product with id %s could not be found!", id));
        }
    }
}
