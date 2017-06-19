# Kibana

1. put index mappings

![put mappings](./putIndexMappings.gif)

   ```json
   PUT ants-idx
   {
     "mappings" : {
      "movements" : {
        "properties" : {
           "id": {"type": "keyword"},
           "x": {"type": "integer"},
           "y": {"type": "integer"},
           "timestamp": {"type": "date"},
           "moved": {"type": "boolean"}
         }
      } 
     }
   }
   ```
   
2. create ants-idx
3. import [AntVisualizations.json](AntVisualizations.json)
3. import [AntDashboard.json](AntDashboard.json)