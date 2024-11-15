package ru.supply;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.supply.data.item.product.Product;
import ru.supply.data.item.product.RpProduct;
import ru.supply.data.item.tool.RpTool;
import ru.supply.data.item.tool.Tool;
import ru.supply.data.utils.item.ItemStatus;
import ru.supply.requestEntity.user.UserRequestEntity;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Tag(name = "Anonymous" , description = "Registration and login")
public class endPoint {

    DataSource dataSource;


    @PostMapping("/register")
    public String register(@Valid @RequestBody UserRequestEntity userRequestEntity) throws SQLException {
//        RpUser rpUser = new RpUser(dataSource);
//        Optional<User> user = rpUser.add(userRequestEntity);
//        Optional<User> getUser = rpUser.getByEmail("gd.host@yandex.ru");
//        Company company = new Company(
//                UUID.randomUUID(),
//                "test",
//                getUser.orElseThrow().id(),
//                List.of(new Email("gd.host@yandex.ru"), new Email("gd.host1@yandex.ru")),
//                List.of(new Phone("+79033073746"), new Phone("+79033073747")),
//                new Bil("test"),
//                new Tax("test"),
//                List.of(),
//                CompanyStatus.ACTIVE
//        );
//        RpCompany rpCompany = new RpCompany(dataSource);
//        rpCompany.add(company);
//        Optional<Company> getCompany = rpCompany.getByAdminId(getUser.orElseThrow().id());
//        Warehouse warehouse = new Warehouse(
//                UUID.randomUUID(),
//                "test",
//                new Address("test"),
//                getCompany.orElseThrow().id(),
//                0L,
//                0L,
//                LocalDate.now(),
//                LocalDate.now()
//        );
//        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);
//        rpWarehouse.add(warehouse);
//        Optional<Warehouse> getWarehouse = rpWarehouse.getById(UUID.fromString("ee1e41ee-5eb2-41e7-a050-726077bc5d36"));
//        Tool tool = new Tool(
//                UUID.randomUUID(),
//                "test",
//                "test",
//                ItemStatus.ACTIVE,
//                "test"
//        );
//        RpTool rpTool = new RpTool(dataSource);
//        Optional<Tool> addtool = rpTool.add(tool);
//        Optional<Tool> gettool = rpTool.get(addtool.orElseThrow().id());
        Optional<Product> product = new RpProduct(dataSource).add(new Product(UUID.randomUUID(), "test", "test", 1, ItemStatus.ACTIVE, LocalDate.now()));
        Optional<Product> getProduct = new RpProduct(dataSource).get(product.orElseThrow().id());
        return "";
    }

    @PostMapping("/r")
    public String r(@RequestBody String m) {
        return m;
    }
    @GetMapping("/")
    public String init() {
        return "Hi";
    }
}
