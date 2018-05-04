package com.sysensor.app.repository;

import com.sysensor.app.model.Bus;
import com.sysensor.app.model.BusOwner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BusOwnerRepositoryTest {
    @Autowired
    BusOwnerRepo busOwnerRepo;
    @Autowired
    BusRepo busRepo;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    public void busOwnerShouldReturnTheCorrectAttributes() {
        Optional<BusOwner> busOwnerOptional = busOwnerRepo.findById("4028818462642c730162642c8d040003");
        Assert.assertTrue(busOwnerOptional.isPresent());
        BusOwner busOwner = busOwnerOptional.get();
        Assert.assertEquals("4028818462642c730162642c8d040003", busOwner.getUuid());
        Assert.assertEquals("Selvam", busOwner.getName());
        Assert.assertEquals("Matara", busOwner.getAddress());
        Assert.assertEquals("0793005675", busOwner.getPhone());
        Assert.assertEquals("selvam", busOwner.getUsername());
        Assert.assertEquals("$2a$10$5SM3OIksgYLL6LU8bb7Raeff2A1nwAuEsF.XoXQq6QxvJwRjh96Jq", busOwner.getPassword());

        Bus bus = busOwner.getBusList().get(0);
        Assert.assertEquals("4028818462642c730162642c8d040008", bus.getUuid());
        Assert.assertEquals("RT120", bus.getRegistration_no());

        Optional<BusOwner> busOwnerOptional2 = busOwnerRepo.findById("4028818462642c730162642c8d040001");
        Assert.assertTrue(busOwnerOptional.isPresent());
        BusOwner busOwner2 = busOwnerOptional2.get();

        Bus bus2 = busOwner2.getBusList().get(0);
        Assert.assertEquals("4028818462642c730162642c8d040009", bus2.getUuid());
        Assert.assertEquals("RT130", bus2.getRegistration_no());

        Bus bus3 = busOwner2.getBusList().get(1);
        Assert.assertEquals("4028818462642c730162642c8d040010", bus3.getUuid());
        Assert.assertEquals("RT400", bus3.getRegistration_no());

    }

    @Test
    @Transactional
    public void busOwnerShouldBeAbleToUpdateAttributes() {
        BusOwner busOwner = new BusOwner();
        busOwner.setName("Damith");
        busOwner.setAddress("Batakettara");
        busOwner.setPhone("3456782345");
        busOwner.setUsername("damith");
        busOwner.setPassword("Wow");

        List<Bus> buses = new ArrayList<>();
        Bus busOne = new Bus();
        busOne.setBusOwner(busOwner);
        busOne.setRegistration_no("RT350");
        buses.add(busOne);

        Bus busTwo = new Bus();
        busTwo.setBusOwner(busOwner);
        busTwo.setRegistration_no("RT200");
        buses.add(busTwo);

        busOwner.setBusList(buses);
        busOwnerRepo.save(busOwner);

        List<BusOwner> list = busOwnerRepo.findAll();
        Assert.assertEquals(4, list.size());

        BusOwner busOwnerAfter = busOwnerRepo.getOne(busOwner.getUuid());
        Assert.assertEquals(2, busOwnerAfter.getBusList().size());

        busOwnerAfter.getBusList().remove(0);
        busOwnerRepo.save(busOwnerAfter);

        BusOwner busOwnerAfterDeleteOne = busOwnerRepo.getOne(busOwner.getUuid());
        Assert.assertEquals(1, busOwnerAfterDeleteOne.getBusList().size());

        busOwnerRepo.deleteById(busOwner.getUuid());

        Optional<BusOwner> busOwnerAfterDelete = busOwnerRepo.findById(busOwner.getUuid());
        Assert.assertFalse(busOwnerAfterDelete.isPresent());

    }


    @Test
    @Transactional
    public void busOwnerRecordShouldRemoveTheBusesWhenDeletedTheBusOwner() {
        BusOwner busOwner = new BusOwner();
        busOwner.setName("Dinuka");
        busOwner.setAddress("Piliyandala");
        busOwner.setPhone("9908768963");
        busOwner.setUsername("dinuka");
        busOwner.setPassword("Nice");

        List<Bus> buses = new ArrayList<>();
        Bus busOne = new Bus();
        busOne.setBusOwner(busOwner);
        busOne.setRegistration_no("RT120");
        buses.add(busOne);

        Bus busTwo = new Bus();
        busTwo.setBusOwner(busOwner);
        busTwo.setRegistration_no("RT138");
        buses.add(busTwo);

        busOwner.setBusList(buses);
        busOwnerRepo.save(busOwner);

        List<BusOwner> list = busOwnerRepo.findAll();
        Assert.assertEquals(4, list.size());

        BusOwner busOwnerAfter = busOwnerRepo.getOne(busOwner.getUuid());
        Assert.assertEquals(busOwner.getUuid(), busOwnerAfter.getUuid());
        Assert.assertEquals("Dinuka", busOwnerAfter.getName());
        Assert.assertEquals("Piliyandala", busOwnerAfter.getAddress());
        Assert.assertEquals("9908768963", busOwnerAfter.getPhone());
        Assert.assertEquals("dinuka", busOwnerAfter.getUsername());
        Assert.assertTrue(passwordEncoder.matches(busOwnerAfter.getPassword(), passwordEncoder.encode("Nice")));

        Bus busAfter1 = busOwnerAfter.getBusList().get(0);
        Assert.assertNotNull(busAfter1.getUuid());
        Assert.assertEquals("RT120", busAfter1.getRegistration_no());
        Assert.assertEquals(busOwnerAfter.getUuid(), busAfter1.getBusOwner().getUuid());

        Bus busAfter2 = busOwnerAfter.getBusList().get(1);
        Assert.assertNotNull(busAfter2.getUuid());
        Assert.assertEquals("RT138", busAfter2.getRegistration_no());
        Assert.assertEquals(busOwnerAfter.getUuid(), busAfter2.getBusOwner().getUuid());

        Bus busFromBusRepo1 = busRepo.getOne(busAfter1.getUuid());
        Assert.assertNotNull(busFromBusRepo1);

        Bus busFromBusRepo2 = busRepo.getOne(busAfter2.getUuid());
        Assert.assertNotNull(busFromBusRepo2);

        busOwnerRepo.delete(busOwnerAfter);

        Optional<Bus> busAfterDeleteOptional1 = busRepo.findById(busAfter1.getUuid());
        Assert.assertFalse(busAfterDeleteOptional1.isPresent());

        Optional<Bus> busAfterDeleteOptional2 = busRepo.findById(busAfter2.getUuid());
        Assert.assertFalse(busAfterDeleteOptional2.isPresent());
    }

    @Test
    @Transactional
    public void busOwnerShouldBeAbleToDeleteTheBuses() {
        BusOwner busOwner = new BusOwner();
        busOwner.setName("Dinuka");
        busOwner.setAddress("Piliyandala");
        busOwner.setPhone("9908768963");
        busOwner.setUsername("dinuka");
        busOwner.setPassword("Nice");

        List<Bus> buses = new ArrayList<>();
        Bus busOne = new Bus();
        busOne.setBusOwner(busOwner);
        busOne.setRegistration_no("RT120");
        buses.add(busOne);

        Bus busTwo = new Bus();
        busTwo.setBusOwner(busOwner);
        busTwo.setRegistration_no("RT138");
        buses.add(busTwo);

        busOwner.setBusList(buses);
        busOwnerRepo.save(busOwner);

        List<BusOwner> list = busOwnerRepo.findAll();
        Assert.assertEquals(4, list.size());

        BusOwner busOwnerAfter = busOwnerRepo.getOne(busOwner.getUuid());
        Assert.assertEquals(2, busOwnerAfter.getBusList().size());

        busOwnerAfter.getBusList().remove(0);
        busOwnerRepo.save(busOwnerAfter);

        BusOwner busOwnerAfterDeleteOne = busOwnerRepo.getOne(busOwner.getUuid());
        Assert.assertEquals(1, busOwnerAfterDeleteOne.getBusList().size());

    }


}