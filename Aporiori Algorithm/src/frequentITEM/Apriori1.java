package frequentITEM;

/*
Java implementation of the Apriori Algorithm
Author: Manav Sanghavi		Author Link: https://www.facebook.com/manav.sanghavi 
www.pracspedia.com

SQL Queries for database:

CREATE TABLE apriori(transaction_id int, object int);

INSERT INTO apriori VALUES(1, 1);
INSERT INTO apriori VALUES(1, 3);
INSERT INTO apriori VALUES(1, 4);
INSERT INTO apriori VALUES(2, 2);
INSERT INTO apriori VALUES(2, 3);
INSERT INTO apriori VALUES(2, 5);
INSERT INTO apriori VALUES(3, 1);
INSERT INTO apriori VALUES(3, 2);
INSERT INTO apriori VALUES(3, 3);
INSERT INTO apriori VALUES(3, 5);
INSERT INTO apriori VALUES(4, 2);
INSERT INTO apriori VALUES(4, 5);

*/

import java.util.*;  
import java.sql.*;

class Tuple { // TUPLE CONSTRUCTOR CLASS
	Set<Integer> itemset;
	int support;

	Tuple() {
		itemset = new HashSet<>();
		support = -1;
	}

	Tuple(Set<Integer> s) {
		itemset = s;
		support = -1;
	}

	Tuple(Set<Integer> s, int i) {
		itemset = s;
		support = i;
	}
}

class Apriori1 {
	static Set<Tuple> c; // CANDIDATE ITEMSET
	static Set<Tuple> l; // FREQUENT ITEMSETS
	static int d[][];
	static int min_support;

	public static void main(String args[]) throws Exception {
		getDatabase(); // TID=ITEM1,ITEM2,.. AVAILABLE NOW
		c = new HashSet<>(); // NO DUPLICATE CANDIDATE
		l = new HashSet<>();
		// Scanner scan = new Scanner(System.in);
		int i, j;
		// System.out.println("Enter the minimum support (as an integer value):");
		min_support = 1;
		Set<Integer> candidate_set = new HashSet<>();
		for (i = 0; i < d.length; i++) {
			System.out.println("Transaction Number: " + (i + 1) + ":");
			for (j = 0; j < d[i].length; j++) {
				System.out.print("Item number " + (j + 1) + " = ");
				System.out.println(d[i][j]);
				candidate_set.add(d[i][j]);
			}
		}

		Iterator<Integer> iterator = candidate_set.iterator();
		while (iterator.hasNext()) {
			Set<Integer> s = new HashSet<>();
			s.add(iterator.next()); // ASSIGN ITEMSET TO "S"
			Tuple t = new Tuple(s, count(s)); // ITEMSET, SUPPORT COUNT
			c.add(t); // TID = ITEM1, ITEM2,...
		}

		prune();
		generateFrequentItemsets();
	}

	static int count(Set<Integer> s) {
		int i, k;
		int support = 0;
		int count;
		int total = d.length;
		int conff = 0;
		boolean containsElement;
		for (i = 0; i < d.length; i++) { // LOOP TO TRANSACTION TOTAL
			count = 0;
			Iterator<Integer> iterator = s.iterator(); // ITEMSET "S" ITERATE
			while (iterator.hasNext()) {
				int element = iterator.next();
				containsElement = false;
				for (k = 0; k < d[i].length; k++) { // LOOP TO ITEM TOTAL OF ONE TRANSACTION TID
					if (element == d[i][k]) {
						containsElement = true;
						count++; // SAME ITEMSET COUNT IN ONE TRANSACTIN TID
						break;
					}
				}
				if (!containsElement) {
					break;
				}
			}
			if (count == s.size()) { // TID TOTAL IN THIS CASE 194400
				support++;
			}
		}
		return support; // SUPPORT ASSIGN TO A TUPLE
	}

	static void prune() throws SQLException, ClassNotFoundException {
		l.clear();

		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/songs?" + "user=root&password=");
		Statement s = con.createStatement();

		Iterator<Tuple> iterator = c.iterator();
		while (iterator.hasNext()) {
			Tuple t = iterator.next();
			if (t.support >= min_support) {
				l.add(t); // ADD TO FREQUENT SET IF CONDITIONS TRUE
			}
		}
		System.out.println("-+- L -+-");
		for (Tuple t : l) { // PRINT OUT FIRST PASS
			System.out.println(t.itemset + " : " + t.support);
			if (t.itemset.size() == 2) {
				/*
				 * int a[]=new int[2]; Iterator<Integer> itr = t.itemset.iterator(); int loop
				 * =0; while(itr.hasNext()) { a[loop] = itr.next(); loop++; }
				 */
				Integer[] int_arr = t.itemset.toArray(new Integer[0]);

				s.executeUpdate(
						"INSERT INTO SNDPASS VALUES (" + int_arr[0] + "," + int_arr[1] + "," + t.support + ");");

			}
			if (t.itemset.size() == 3) {
				Integer[] int_arr = t.itemset.toArray(new Integer[0]);

				s.executeUpdate(
						"INSERT INTO thdpass VALUES (" + int_arr[0] + "," + int_arr[1] + "," +int_arr[2] + "," + t.support + ");");
			}
			
			if (t.itemset.size() == 4) {
				Integer[] int_arr = t.itemset.toArray(new Integer[0]);

				s.executeUpdate(
						"INSERT INTO fthpass VALUES (" + int_arr[0] + "," + int_arr[1] + "," +int_arr[2]+ "," + int_arr[3] + "," + t.support + ");");
			}


		}
	}

	static void generateFrequentItemsets() throws ClassNotFoundException, SQLException {
		boolean toBeContinued = true;
		int element = 0;
		int size = 1;
		Set<Set> candidate_set = new HashSet<>();
		while (toBeContinued) {
			candidate_set.clear();
			c.clear(); // FRIST CANDIDATE LIST CLEAR
			Iterator<Tuple> iterator = l.iterator(); // FIRST PASS FREQUENT SETS
			while (iterator.hasNext()) {
				Tuple t1 = iterator.next();
				Set<Integer> temp = t1.itemset; // EACH SONGID FROM FIRST PASS 1
				Iterator<Tuple> it2 = l.iterator();
				while (it2.hasNext()) {
					Tuple t2 = it2.next();
					Iterator<Integer> it3 = t2.itemset.iterator();// EACH SONGID FROM FIRST PASS2
					while (it3.hasNext()) {
						try {
							element = it3.next(); // EACH SONGID FROM FIRST PASS 3
						} catch (ConcurrentModificationException e) {
							// Sometimes this Exception gets thrown, so simply break in that case.
							break;
						}
						temp.add(element);
						if (temp.size() != size) { // I => K size = 2
							Integer[] int_arr = temp.toArray(new Integer[0]);
							Set<Integer> temp2 = new HashSet<>();
							for (Integer x : int_arr) {
								temp2.add(x);
							}
							candidate_set.add(temp2);
							temp.remove(element);
						}
					}
				}
			}
			Iterator<Set> candidate_set_iterator = candidate_set.iterator();
			while (candidate_set_iterator.hasNext()) {
				Set s = candidate_set_iterator.next();
				// These lines cause warnings, as the candidate_set Set stores a raw set.
				c.add(new Tuple(s, count(s)));
			}
			prune();
			if (l.size() <= 1) {
				toBeContinued = false;
			}
			size++;
		}
		System.out.println("\n=+= FINAL LIST =+=");
		for (Tuple t : l) {
			System.out.println(t.itemset + " : " + t.support);
		}
	}

	static void getDatabase() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/songs?" + "user=root&password=");
		Statement s = con.createStatement();

		ResultSet rs = s.executeQuery("SELECT * FROM map;");
		Map<Integer, List<Integer>> m = new HashMap<>(); // USE LIST HERE TO ORDER TID
		List<Integer> temp;
		while (rs.next()) {
			int list_no = Integer.parseInt(rs.getString(1)); // GET TID
			int object = Integer.parseInt(rs.getString(2)); // GET ITEM
			temp = m.get(list_no); // RETURN VALUE MAP WITH KEY
			if (temp == null) { // CHEKC IF THE THE VALUE IS NULL
				temp = new LinkedList<>();
			}
			temp.add(object); // ORDER TID
			m.put(list_no, temp); // PUT IT IN THE MAP
		}

		Set<Integer> keyset = m.keySet(); // NO DUPLICATE
		d = new int[keyset.size()][]; // 2D ARRAY FOR EACH TID=ITEM1,ITEM2,..
		Iterator<Integer> iterator = keyset.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			temp = m.get(iterator.next());
			Integer[] int_arr = temp.toArray(new Integer[0]); // TID=ITEM1,ITEM2,..
			d[count] = new int[int_arr.length];
			for (int i = 0; i < d[count].length; i++) {
				d[count][i] = int_arr[i].intValue();
			}
			count++;
		}
	}
}

/*
 * 
 * OUTPUT:
 * 
 * Enter the minimum support (as a floating point value, 0<x<1): 0.5 Transaction
 * Number: 1: Item number 1 = 1 Item number 2 = 3 Item number 3 = 4 Transaction
 * Number: 2: Item number 1 = 2 Item number 2 = 3 Item number 3 = 5 Transaction
 * Number: 3: Item number 1 = 1 Item number 2 = 2 Item number 3 = 3 Item number
 * 4 = 5 Transaction Number: 4: Item number 1 = 2 Item number 2 = 5 -+- L -+-
 * [1] : 2 [3] : 3 [2] : 3 [5] : 3 -+- L -+- [2, 3] : 2 [3, 5] : 2 [1, 3] : 2
 * [2, 5] : 3 -+- L -+- [2, 3, 5] : 2
 * 
 * =+= FINAL LIST =+= [2, 3, 5] : 2
 */
