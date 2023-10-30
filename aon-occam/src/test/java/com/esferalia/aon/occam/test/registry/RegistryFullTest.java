package com.esferalia.aon.occam.test.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;

/**
 * Tests for the methods for the classes RegistryDAO and RegistryFull
 */
public class RegistryFullTest extends AbstractOccamTest {
	
	// ------------------------------------------ REGISTRY
	
	/**
	 * Tests the methods of Registry from the class RegistryFull
	 */
	@Test
	public void RegistryTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonFaker.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		Registry inserted = registryFull.getRegistry();
		
		Asserts.assertEqualsRegistry(registry, inserted);
		assertEquals(registry.getId(), inserted.getId());
		assertEquals(registry.getDomain(), inserted.getDomain());
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		RegistryDAO.delete(ctx, registryFull);
		
		assertNull(RegistryDAO.get(ctx, registryFull.getId()));
	}
	
	/**
	 * Tests the method delete from the class RegistryDAO
	 */
	@Test
	public void deleteTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonFaker.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryBank deleted = AonFaker.getRegistryBank(ctx);
		deleted.setRegistry(registry.getId());
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		RegistryDAO.delete(ctx, registryFull);
		
		assertNull(RegistryDAO.get(ctx, registryFull.getId()));
	}
	
	// ------------------------------------------ REGISTRY BANK
	

	/**
	 * Tests the method fillChilds from the class RegistryDAO with RegistryBank
	 */
	@Test
	public void fillChildsRegistryBankTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		List<RegistryBank> registryBankList = registryFull.getBanks();

		registryBankList.forEach(f -> assertEquals(f.getRegistry(), registry.getId()));
	}
	
	/**
	 * Tests the method saveChilds from the class RegistryDAO with RegistryBank
	 */
	@Test
	public void saveChildsRegistryBankTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		RegistryBank removed = AonFaker.getRegistryBank(ctx);
		removed.setRegistry(registry.getId());
		removed.setRemoved(true);
		
		RegistryBank dirty = AonFaker.getRegistryBank(ctx);
		dirty.setRegistry(registry.getId());
		dirty.setDirty(true);
		
		RegistryBankDAO.save(ctx, removed);
		RegistryBankDAO.save(ctx, dirty);
		
		registryFull.addBank(removed);
		registryFull.addBank(dirty);
				
		List<RegistryBank> registryBankList = registryFull.getBanks();
		assertTrue(registryBankList.contains(removed));
		assertTrue(registryBankList.contains(dirty));

		RegistryDAO.saveChilds(ctx, registryFull);
		
		registryBankList = registryFull.getBanks();
		assertTrue(RegistryBankDAO.get(ctx, removed.getId()).isEmpty());
	}
	
	/**
	 * Tests the method getBanks from the class RegistryFull
	 */
	@Test
	public void getBanks() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setRegistry(registry.getId());
		
		registryFull.addBank(registryBank);
		
		List<RegistryBank> registryBankList = registryFull.getBanks();
		
		assertFalse(registryBankList.isEmpty());
	}
	
	/**
	 * Tests the method setBanks from the class RegistryFull
	 */
	@Test
	public void setBanks() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		registryFull.setBanks(new LinkedList<RegistryBank>());
		List<RegistryBank> registryBankList = registryFull.getBanks();
		
		assertTrue(registryBankList.isEmpty());
	}
	
	/**
	 * Tests the method addBanks from the class RegistryFull
	 */
	@Test
	public void addBanks() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setRegistry(registry.getId());
		
		registryFull.addBank(registryBank);
		
		List<RegistryBank> registryBankList = registryFull.getBanks();
		assertTrue(registryBankList.contains(registryBank));
	}
	
	/**
	 * Tests the method hasBanks from the class RegistryFull
	 */
	@Test
	public void hasBanks() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryDAO.fillChilds(ctx, registryFull);
		
		RegistryBank registryBank = AonFaker.getRegistryBank(ctx);
		registryBank.setRegistry(registry.getId());
		registryFull.addBank(registryBank);
		
		assertTrue(registryFull.hasBanks());
		
		registryFull.setBanks(new LinkedList<RegistryBank>());
		
		assertFalse(registryFull.hasBanks());
	}
	
	// ------------------------------------------ REGISTRY ADDRESS
	
	/**
	 * Tests the method fillChilds from the class RegistryDAO with RegistryAddress
	 */
	@Test
	public void fillChildsRegistryAddressTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		List<RegistryAddress> registryAddressList = registryFull.getAddresses();

		registryAddressList.forEach(f -> assertEquals(f.getRegistry(), registry.getId()));
	}
	
	/**
	 * Tests the method saveChilds from the class RegistryDAO with RegistryAddress
	 */
	@Test
	public void saveChildsRegistryAddressTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		RegistryAddress removed = AonFaker.getRegistryAddress(ctx);
		removed.setRegistry(registry.getId());
		removed.setRemoved(true);
			
		RegistryAddress dirty = AonFaker.getRegistryAddress(ctx);
		dirty.setRegistry(registry.getId());
		dirty.setDirty(true);
			
		RegistryAddressDAO.save(ctx, removed);
		RegistryAddressDAO.save(ctx, dirty);
			
		registryFull.addAddress(removed);
		registryFull.addAddress(dirty);
					
		List<RegistryAddress> registryAddressList = registryFull.getAddresses();
		assertTrue(registryAddressList.contains(removed));
		assertTrue(registryAddressList.contains(dirty));
		
		RegistryDAO.saveChilds(ctx, registryFull);
			
		registryAddressList = registryFull.getAddresses();
		assertNull(RegistryAddressDAO.get(ctx, removed.getId()));
	}
	
	/**
	 * Tests the method getAddress from the class RegistryFull
	 */
	@Test
	public void getAddresses() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		RegistryAddress registryAddress = AonFaker.getRegistryAddress(ctx);
		registryAddress.setRegistry(registry.getId());
			
		registryFull.addAddress(registryAddress);
			
		List<RegistryAddress> registryAddressList = registryFull.getAddresses();
			
		assertFalse(registryAddressList.isEmpty());
	}
	
	/**
	 * Tests the method setAddress from the class RegistryFull
	 */
	@Test
	public void setAddresses() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		registryFull.setAddresses(new LinkedList<RegistryAddress>());
		List<RegistryAddress> registryAddressList = registryFull.getAddresses();
			
		assertTrue(registryAddressList.isEmpty());
	}
	
	/**
	 * Tests the method addAddress from the class RegistryFull
	 */
	@Test
	public void addAddress() {
	RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
	Registry registry = AonRandom.getRegistry(ctx);
	registryFull.setRegistry(registry);
			
	RegistryDAO.fillChilds(ctx, registryFull);
			
	RegistryAddress registryAddress = AonFaker.getRegistryAddress(ctx);
	registryAddress.setRegistry(registry.getId());
			
	registryFull.addAddress(registryAddress);
			
	List<RegistryAddress> registryAddressList = registryFull.getAddresses();
	assertTrue(registryAddressList.contains(registryAddress));
	}
	
	/**
	 * Tests the method hasAddress from the class RegistryFull
	 */
	@Test
	public void hasAddress() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		RegistryAddress registryAddress = AonFaker.getRegistryAddress(ctx);
		registryAddress.setRegistry(registry.getId());
		registryFull.addAddress(registryAddress);
			
		assertTrue(registryFull.hasAddresses());
			
		registryFull.setAddresses(new LinkedList<RegistryAddress>());
			
		assertFalse(registryFull.hasAddresses());
	}
	
	
	// ------------------------------------------ REGISTRY MEDIA
	
	/**
	 * Tests the method fillChilds from the class RegistryDAO with RegistryMedia
	 */
	@Test
	public void fillChildsRegistryMediaTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		List<RegistryMedia> registryMediaList = registryFull.getMedias();

		registryMediaList.forEach(f -> assertEquals(f.getRegistry(), registry.getId()));
	}
		
	/**
	 * Tests the method saveChilds from the class RegistryDAO with RegistryMedia
	 */
	@Test
	public void saveChildsRegistryMediaTest() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		RegistryMedia removed = AonFaker.getRegistryMedia(ctx);
		removed.setRegistry(registry.getId());
		removed.setRemoved(true);
			
		RegistryMedia dirty = AonFaker.getRegistryMedia(ctx);
		dirty.setRegistry(registry.getId());
		dirty.setDirty(true);
			
		RegistryMediaDAO.save(ctx, removed);
		RegistryMediaDAO.save(ctx, dirty);
			
		registryFull.addMedia(removed);
		registryFull.addMedia(dirty);
					
		List<RegistryMedia> registryMediaList = registryFull.getMedias();
		assertTrue(registryMediaList.contains(removed));
		assertTrue(registryMediaList.contains(dirty));

		RegistryDAO.saveChilds(ctx, registryFull);
			
		registryMediaList = registryFull.getMedias();
		assertNull(RegistryMediaDAO.get(ctx, removed.getId()));
	}
	
	/**
	 * Tests the method getMedias from the class RegistryFull
	 */
	@Test
	public void getMedia() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		RegistryMedia registryMedia = AonFaker.getRegistryMedia(ctx);
		registryMedia.setRegistry(registry.getId());
			
		registryFull.addMedia(registryMedia);
			
		List<RegistryMedia> registryMediaList = registryFull.getMedias();
			
		assertFalse(registryMediaList.isEmpty());
	}
	
	/**
	 * Tests the method setMedias from the class RegistryFull
	 */
	@Test
	public void setMedias() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		registryFull.setMedias(new LinkedList<RegistryMedia>());
		List<RegistryMedia> registryAddressList = registryFull.getMedias();
			
		assertTrue(registryAddressList.isEmpty());
	}
	
	/**
	 * Tests the method addMedias from the class RegistryFull
	 */
	@Test
	public void addMedias() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
		
		RegistryDAO.fillChilds(ctx, registryFull);
			
		RegistryMedia registryMedia = AonFaker.getRegistryMedia(ctx);
		registryMedia.setRegistry(registry.getId());
			
		registryFull.addMedia(registryMedia);
			
		List<RegistryMedia> registryMediaList = registryFull.getMedias();
		assertTrue(registryMediaList.contains(registryMedia));
	}
	
	/**
	 * Tests the method hasMedias from the class RegistryFull
	 */
	@Test
	public void hasMedias() {
		RegistryFull<Registry> registryFull = new RegistryFull<Registry>();
		Registry registry = AonRandom.getRegistry(ctx);
		registryFull.setRegistry(registry);
			
		RegistryDAO.fillChilds(ctx, registryFull);
			
		RegistryMedia registryMedia = AonFaker.getRegistryMedia(ctx);
		registryMedia.setRegistry(registry.getId());
		registryFull.addMedia(registryMedia);
			
		assertTrue(registryFull.hasMedias());
			
		registryFull.setMedias(new LinkedList<RegistryMedia>());
			
		assertFalse(registryFull.hasMedias());
	}
}
