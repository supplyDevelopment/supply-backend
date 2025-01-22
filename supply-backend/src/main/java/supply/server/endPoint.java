package supply.server;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import supply.server.data.company.Company;
import supply.server.data.company.CreateCompany;
import supply.server.data.supplier.RpSupplier;
import supply.server.data.supplier.Supplier;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.service.dataService.RepositoryService;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Tag(name = "Anonymous" , description = "Registration and login")
public class endPoint {

    RepositoryService repositoryService;


//    @PostMapping("/register")
//    public String register(@Valid @RequestBody UserRequestEntity userRequestEntity) throws SQLException {
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
//        Optional<Product> product = new RpProduct(dataSource).add(new Product(UUID.randomUUID(), "test", "test", 1, ItemStatus.ACTIVE, LocalDate.now()));
//        Optional<Product> getProduct = new RpProduct(dataSource).get(product.orElseThrow().id());
//        Optional<Supplier> supplier = new RpSupplier(dataSource).add(new Supplier(UUID.randomUUID(), "test", List.of(), List.of(), "test", LocalDate.now(), null));
//        Optional<Supplier> getSupplier = new RpSupplier(dataSource).getById(supplier.orElseThrow().id());
//        return "";
//    }
//
//    @PostMapping("/r")
//    public String r(@RequestBody String m) {
//        return m;
//    }
    @GetMapping("/")
    public String init() throws SQLException {
        Company company = repositoryService.getCompany().add(new CreateCompany("test", List.of(), List.of(), new Bil("test"), new Tax("test"), List.of(), CompanyStatus.ACTIVE));
        return repositoryService.getCompany().get(company.id()).name();
    }
}
