package com.jpa.test;

import javax.persistence.EntityManager;

public class Main {
  public static void main(String[] args) {
    
    Test test = new Test();
    test.setId(new Integer(38));
    test.setText("testing jpa");
 
    EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
    System.out.println("entity manager created:::: "+em);
    
    readExistingValue(em);
    
    em.getTransaction()
        .begin();
    System.out.println("beginning the trasaction:::: ");
    em.persist(test);
    em.getTransaction()
        .commit();
    System.out.println("commit done-------");
    em.close();
    PersistenceManager.INSTANCE.close();
  }
  
	private static void readExistingValue(EntityManager em) {
		System.out.println("reading existing value::::::: "+em);
		Test testObject = em.find(Test.class, 100);
		if(testObject != null){
			System.out.println("id=====: "+testObject.getId());
			System.out.println("text======: "+testObject.getText());
		}
	}
	
}