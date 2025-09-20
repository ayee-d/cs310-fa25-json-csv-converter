package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.io.StringWriter;




public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
    
    
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
    
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            
            CSVReader reader = new CSVReader(new StringReader(csvString));
            
            String[] header = reader.readNext();
            if (header == null) {
                reader.close();
                return result.trim();
            }
            
            JsonArray colHeadings = new JsonArray();
            for (String h : header) {
                colHeadings.add(h);
            }
            
            
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();
            
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length == 0) continue;
                
                prodNums.add(row[0]);
                
                JsonArray one = new JsonArray();
                one.add(row[1]);
                one.add(Integer.parseInt(row[2].trim()));
                one.add(Integer.parseInt(row[3].trim()));
                one.add(row[4]);
                one.add(row[5]);
                one.add(row[6]);
                data.add(one);
            }
            reader.close();
            
            
            Map<String, Object> root = new LinkedHashMap();
                root.put("ProdNums", prodNums);
                root.put("ColHeadings", colHeadings);
                root.put("Data", data);
                
                result = Jsoner.serialize(root);
            
        
               
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            Map<String, Object> root = (Map<String, Object>) Jsoner.deserialize(jsonString);
            
            List<?> colHeadings = (List<?>) root.get("ColHeadings");
            List<?> prodNums = (List<?>) root.get("ProdNums");
            List<?> data = (List<?>) root.get("Data");
            
            StringWriter out = new StringWriter();
            CSVWriter writer = new CSVWriter(out);
            
            String[] header = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++){
                header[i] = String.valueOf(colHeadings.get(i));
                
            }
            writer.writeNext(header, true);
        
            for (int i = 0; i < data.size(); i++) {
                List<?> row = (List<?>) data.get(i);
                
                String prod = String.valueOf(prodNums.get(i));
                String title = String.valueOf(row.get(0));
                
                int season = ((Number) row.get(1)).intValue();
                int episode = ((Number) row.get(2)).intValue();
                
                String seasonStr = String.valueOf(season);
                String episodeStr = (episode < 10) ? ("0" + episode) : String.valueOf(episode);
                String stardate = String.valueOf(row.get(3));
                String original = String.valueOf(row.get(4));
                String remaster = String.valueOf(row.get(5));
                
                writer.writeNext(
                        new String[]{ prod, title, seasonStr, episodeStr, stardate, original, remaster}, true);
                
                        
            }
            
            writer.flush();
            writer.close();
            result = out.toString();
            
         
          
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
