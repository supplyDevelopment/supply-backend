package ru.supply;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.supply.data.company.Company;
import ru.supply.data.company.RpCompany;
import ru.supply.data.user.RpUser;
import ru.supply.data.user.User;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.company.Bil;
import ru.supply.data.utils.company.CompanyStatus;
import ru.supply.data.utils.company.Tax;
import ru.supply.data.utils.user.UserName;
import ru.supply.requestEntity.user.UserRequestEntity;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
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
        RpUser rpUser = new RpUser(dataSource);
//        Optional<User> user = rpUser.add(userRequestEntity);
        Optional<User> getUser = rpUser.getByEmail("gd.host@yandex.ru");
        Company company = new Company(
                UUID.randomUUID(),
                "test",
                getUser.orElseThrow().id(),
                List.of(new Email("gd.host@yandex.ru"), new Email("gd.host1@yandex.ru")),
                List.of(new Phone("+79033073746"), new Phone("+79033073747")),
                new Bil("test"),
                new Tax("test"),
                List.of(),
                CompanyStatus.ACTIVE
        );
        RpCompany rpCompany = new RpCompany(dataSource);
//        rpCompany.add(company);
        Optional<Company> getCompany = rpCompany.getByAdminId(getUser.orElseThrow().id());
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