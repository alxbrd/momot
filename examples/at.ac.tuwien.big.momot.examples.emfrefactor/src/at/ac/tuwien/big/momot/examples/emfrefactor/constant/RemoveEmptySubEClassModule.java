package at.ac.tuwien.big.momot.examples.emfrefactor.constant;

public interface RemoveEmptySubEClassModule {
   public static interface RemoveEmptySubEclass {
      static interface Parameter {}

      static String NAME = RemoveEmptySubEClassModule.MODULE + RemoveEmptySubEClassModule.SEPARATOR
            + "removeEmptySubEclass";
   }

   static String SEPARATOR = "::";
   static String FILE_NAME = "remove_empty_sub_eclass";
   static String MODULE_NAME = "RemoveEmptySubEClass";

   static String MODULE = FILE_NAME + SEPARATOR + MODULE_NAME;

}
