package vesna;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RCCMap {
    // Enum per definire le relazioni RCC di base
    public enum RCCRelation {
        DC, // Disconnected (disconnesso)
        EC, // Externally Connected (esternamente connesso)
        PO, // Partially Overlapping (parzialmente sovrapposto)
        EQ, // Equal (uguale)
        TPP, // Tangential Proper Part (parte tangente)
        TPPi, // Tangential Proper Part Inverse (parte tangente inversa)
        NTPP, // Non-Tangential Proper Part (parte propria non tangente)
        NTPPi // Non-Tangential Proper Part Inverse (parte propria non tangente inversa)
    }
    
    // Mappa per memorizzare le relazioni tra le regioni
    private final Map<String, Map<String, RCCRelation>> regionRelations;

    // Costruttore
    public RCCMap() {
        regionRelations = new HashMap<>();
    }

    // Metodo per aggiungere una relazione tra due regioni
    public void addRelation(String regionA, String regionB, RCCRelation relation) {
        regionRelations.putIfAbsent(regionA, new HashMap<>());
        regionRelations.get(regionA).put(regionB, relation);
    }

    // Metodo per ottenere la relazione tra due regioni
    public RCCRelation getRelation(String regionA, String regionB) {
        return regionRelations.getOrDefault(regionA, new HashMap<>()).get(regionB);
    }

    // Metodo per rimuovere una relazione tra due regioni
    public void removeRelation(String regionA, String regionB) {
        if (regionRelations.containsKey(regionA)) {
            regionRelations.get(regionA).remove(regionB);
            if (regionRelations.get(regionA).isEmpty()) {
                regionRelations.remove(regionA);
            }
        }
    }

    // Metodo per ottenere tutte le relazioni di una regione specifica
    public Map<String, RCCRelation> getRelations(String region) {
        return regionRelations.getOrDefault(region, new HashMap<>());
    }

    // Metodo per ottenere tutte le regioni che hanno una certa relazione con una regione specifica
    public Set<String> getRegionsWithRelation(String region, RCCRelation relation) {
        Set<String> relatedRegions = new HashSet<>();
        Map<String, RCCRelation> relations = regionRelations.get(region);
        if (relations != null) {
            for (Map.Entry<String, RCCRelation> entry : relations.entrySet()) {
                if (entry.getValue() == relation) {
                    relatedRegions.add(entry.getKey());
                }
            }
        }
        return relatedRegions;
    }
}
