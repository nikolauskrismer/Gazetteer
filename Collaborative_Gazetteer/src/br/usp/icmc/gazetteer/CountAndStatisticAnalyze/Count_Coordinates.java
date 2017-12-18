/*
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.icmc.gazetteer.CountAndStatisticAnalyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates.Out_Polygon;
import br.usp.icmc.gazetteer.TAD.Place;
import br.usp.icmc.gazetteer.TAD.Repository;
import br.usp.icmc.gazetteer.cluster.Star_algorithm;

import com.bbn.openmap.geo.OMGeo;

public class Count_Coordinates {
	
	private static final int today =2015;
	
	
	private static boolean search(ArrayList<Integer>pl, int year){
		for(int i=0; i<pl.size();i++){
			if(pl.get(i)==year || year >today)
				return false;
		}
		return true;
	}
	
	public static int [][] countDate( ArrayList<Place> original_places, OMGeo.Polygon poly){
		Place place_min = null, place_max = null;
		ArrayList<Place> places =  (ArrayList<Place>) original_places.clone();
		
		ArrayList<Integer> pl = new ArrayList<Integer>();
		ArrayList<Integer[]> years = new ArrayList<Integer[]>();
	    
		while(!places.isEmpty()){
			if(search(pl,places.get(0).getYear())){
				pl.add(places.get(0).getYear());
			}
			places.remove(0);
		}
		Star_algorithm.fLogger.log(Level.SEVERE,"Fez a busca");
		places.clear();
		places =  (ArrayList<Place>) original_places.clone();
		
		int []tmp = new int[pl.size()]; 
		for(int i=0;i<pl.size();i++){
			tmp[i]= pl.get(i);
		}
		Arrays.sort(tmp);
		Star_algorithm.fLogger.log(Level.SEVERE,"Ordenou");
		for(int i=0;i<tmp.length;i++){
			years.add(new Integer[]{tmp[i],0});
		}
		Star_algorithm.fLogger.log(Level.SEVERE,"Vai computar os anos... tam("+years.size()+")");
		for(int i=0;i<years.size();i++){
			int year = years.get(i)[0];
			int count =0;
			for(int k=0;k<places.size();k++){
				if((year == places.get(k).getYear()) && places.get(k).getGeometry()!=null ){
					Out_Polygon out = new Out_Polygon();
					if(out.insidePolygon(poly, places.get(k).getGeometry())){
						count++;
					
					}
				}
			}
			years.get(i)[1]=count;
		}
		Star_algorithm.fLogger.log(Level.SEVERE,"Criando a matriz...");
		int [][] mat = new int [years.size()][2];
		int i=0;
		for(Integer[]temp:years){
			mat[i][0]=temp[0];
			mat[i][1]=temp[1];
			i++;
		}
		return mat;
	}
    
	public static void build_csv(int [][] years, String name) throws IOException{
		
		File file = new File(name+".csv");
	    // creates the file
	    file.createNewFile();
	    // creates a FileWriter Object
	    FileWriter writer = new FileWriter(file); 
	    writer.write("Date,Coordinates \n");  
		for(int j=0;j<years.length;j++){			
			 writer.write(years[j][0]+","+(years[j][1])+"\n");
			 writer.flush();
		}
		writer.close();
		
	}
}
