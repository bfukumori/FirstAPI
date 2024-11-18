package br.com.fiap.resource;

import br.com.fiap.dao.ProductDAO;
import br.com.fiap.dto.ProductDetailsDTO;
import br.com.fiap.dto.RegisterProductDTO;
import br.com.fiap.dto.UpdateProductDTO;
import br.com.fiap.exception.NotFoundException;
import br.com.fiap.model.Product;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Path("products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
    private final ProductDAO productDAO;

    public ProductResource() throws SQLException {
        productDAO = new ProductDAO();
    }

    @POST
    public Response register(RegisterProductDTO productDto, @Context UriInfo uriInfo) throws SQLException {
        ModelMapper mapper = new ModelMapper();
        Product product = mapper.map(productDto, Product.class);
        productDAO.insert(product);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        uri.path(String.valueOf(product.getId()));
        return Response.created(uri.build()).entity(mapper.map(product, ProductDetailsDTO.class)).build();
    }

    @GET
    public List<ProductDetailsDTO> getAll() throws SQLException {
        ModelMapper mapper = new ModelMapper();

        return productDAO.getAll().stream().map(m -> mapper.map(m, ProductDetailsDTO.class)).collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) throws SQLException {
        ModelMapper mapper = new ModelMapper();
        try {
            Product product = productDAO.findById(id);
            return Response.ok(mapper.map(product, ProductDetailsDTO.class)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, UpdateProductDTO productDto) throws SQLException {
        ModelMapper mapper = new ModelMapper();
        Product product = mapper.map(productDto, Product.class);
        product.setId(id);
        try {
            productDAO.update(product);
            return Response.ok(mapper.map(product, ProductDetailsDTO.class)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) throws SQLException {
        try {
            productDAO.delete(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PATCH
    @Path("{id}")
    public Response partialUpdate(@PathParam("id") Long id, UpdateProductDTO productDto) throws SQLException {
        ModelMapper mapper = new ModelMapper();
        try {
            Product existingProduct = productDAO.findById(id);
            if (productDto.getName() != null) {
                existingProduct.setName(productDto.getName());
            }
            if (productDto.getDescription() != null) {
                existingProduct.setDescription(productDto.getDescription());
            }
            if (productDto.getPrice() != null) {
                existingProduct.setPrice(productDto.getPrice());
            }
            if (productDto.getStock() != null) {
                existingProduct.setStock(productDto.getStock());
            }
            productDAO.update(existingProduct);
            return Response.ok(mapper.map(existingProduct, ProductDetailsDTO.class)).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("search")
    public List<ProductDetailsDTO> getByFilter(@QueryParam("productName") String productName) throws SQLException {
        ModelMapper mapper = new ModelMapper();

        return productDAO.findByName(productName).stream().map(m -> mapper.map(m, ProductDetailsDTO.class)).collect(Collectors.toList());
    }
}
