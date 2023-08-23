package antifraud.configurations;

import antifraud.models.Region;
import antifraud.models.Role;
import antifraud.models.Status;
import antifraud.repositories.RegionRepository;
import antifraud.repositories.RoleRepository;
import antifraud.repositories.StatusRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadData {
    private final RoleRepository roleRepository;

    private final RegionRepository regionRepository;

    private final StatusRepository statusRepository;

    public LoadData(RoleRepository roleRepository, RegionRepository regionRepository, StatusRepository statusRepository) {
        this.roleRepository = roleRepository;
        this.regionRepository = regionRepository;
        this.statusRepository = statusRepository;
        createRoles();
        createRegions();
        createStatus();
    }

    private void createRoles() {
        if (roleRepository.findAll().isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMINISTRATOR"));
            roleRepository.save(new Role("ROLE_SUPPORT"));
            roleRepository.save(new Role("ROLE_MERCHANT"));
        }
    }

    private void createRegions() {
        if (regionRepository.findAll().isEmpty()) {
            regionRepository.save(new Region("EAP", "East Asia and Pacific"));
            regionRepository.save(new Region("ECA", "Europe and Central Asia"));
            regionRepository.save(new Region("HIC", "High-Income countries"));
            regionRepository.save(new Region("LAC", "Latin America and the Caribbean"));
            regionRepository.save(new Region("MENA", "The Middle East and North Africa"));
            regionRepository.save(new Region("SA", "South Asia"));
            regionRepository.save(new Region("SSA", "Sub-Saharan Africa"));
        }
    }

    private void createStatus() {
        if (statusRepository.findAll().isEmpty()) {
            statusRepository.save(new Status("ALLOWED"));
            statusRepository.save(new Status("MANUAL_PROCESSING"));
            statusRepository.save(new Status("PROHIBITED"));
        }
    }
}
