public class Test
{
    public static void main(String [] args)
    {
        PHPArray<Integer> arr = new PHPArray<Integer>(10);
        System.out.println(arr.hash(6));
        System.out.println(arr.hash("abc"));
        arr.put(6, 1);
        arr.put("Hello", 2);
        System.out.println(arr.get(6));
        System.out.println(arr.get("abc"));
        System.out.println("Linked check: " + arr.linkedGet("6"));
        System.out.println("ArrayList: " + arr.sortableValues.get(1));
        for (int i = arr.hash(6); i < arr.length(); i++)
        {
            System.out.println(arr.each().key);
            System.out.println(arr.each().key);
            //System.out.println(arr.each());
            System.out.println(arr.pairs[i].getKey()); break;
        }
        System.out.println("Check: " + arr.sortableValues.size());

        System.out.println("Before sort?");
        arr.showTable();
        System.out.println("Sort?");
        arr.asort();
        System.out.println("ArrayList: " + arr.sortableValues.get(1));
        arr.showTable();
        arr.sort();
        System.out.println("ArrayList: " + arr.sortableValues.get(1));
        arr.showTable();


       /*arr.unset(6);
       System.out.println("unset done");
       for (int i = arr.hash("abc"); i < arr.length(); i++)
       {
           System.out.println(arr.root.getKey());
           System.out.println(arr.pairs[i].getKey()); break;
       }*/
    }
}
