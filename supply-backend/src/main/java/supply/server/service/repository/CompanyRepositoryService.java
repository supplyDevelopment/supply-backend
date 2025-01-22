package supply.server.service.repository;

import lombok.AllArgsConstructor;
import supply.server.configuration.exception.DataNotFound;
import supply.server.configuration.exception.DbException;
import supply.server.data.InMemoryRepository;
import supply.server.data.PgRepository;
import supply.server.data.Redis;
import supply.server.data.company.Company;
import supply.server.data.company.CreateCompany;
import supply.server.data.company.InMemoryRpCompany;
import supply.server.data.company.RpCompany;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class CompanyRepositoryService {

    private final RpCompany rpCompany;

    private final Redis<Company> inMemoryRpCompany;

    public Company add(CreateCompany createCompany) {
        Company company;
        try {
            Optional<Company> companyOpt = rpCompany.add(createCompany);

            if (companyOpt.isPresent()) {
                company = companyOpt.get();
                inMemoryRpCompany.set(company.id(), company);
            } else {
                throw new DbException("Failed to add company");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return company;
    }

    public Company get(UUID companyId) {
        Company company;
        try {
            Optional<Company> companyOpt = inMemoryRpCompany.get(companyId);

            if (companyOpt.isEmpty()) {
                companyOpt = rpCompany.get(companyId);
                if (companyOpt.isPresent()) {
                    company = companyOpt.get();
                    inMemoryRpCompany.set(company.id(), company);
                } else {
                    throw new DataNotFound("Company with id " + companyId + " not found");
                }
            }
            company = companyOpt.get();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return company;
    }

    public boolean projectCheck(UUID projectId, UUID companyId) {
        boolean result;
        try {
            result = rpCompany.projectCheck(projectId, companyId);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return result;
    }

    public boolean warehouseCheck(UUID warehouseId, UUID companyId) {
        boolean result;
        try {
            result = rpCompany.warehouseCheck(warehouseId, companyId);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return result;
    }

    public boolean userCheck(UUID userId, UUID companyId) {
        boolean result;
        try {
            result = rpCompany.userCheck(userId, companyId);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return result;
    }

    public boolean resourceCheck(UUID resourceId, UUID companyId) {
        boolean result;
        try {
            result = rpCompany.resourceCheck(resourceId, companyId);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return result;
    }

}
