package de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.adapter.in.web;

import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.adapter.in.web.resource.BearbeiterResource;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.adapter.in.web.resource.TagesplanResource;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.adapter.in.web.resource.WerkstattplanResource;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.adapter.in.web.resource.WerkstattterminResource;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.domain.model.bearbeiter.Bearbeiter;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.domain.model.bearbeiter.BearbeiterId;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.domain.model.bearbeiter.Name;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.domain.model.bearbeiter.Vorname;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.domain.model.tagesplan.Tagesplan;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.domain.model.werkstattplan.Werkstattplan;
import de.novatec.autohaus.werkstatt.werkstattplanung.werkstattplan.domain.model.werkstatttermin.*;
import de.novatec.autohaus.werkstatt.jmolecules.Mapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Mapper
@Component
public class WerkstattplanResourceDomainMapper {

    public WerkstattplanResource mapWerkstattplanDtoToWerkstattplanResource(Werkstattplan werkstattplan) {
        WerkstattplanResource resource = new WerkstattplanResource();
        resource.setTagesplaene(werkstattplan.getTagesplaene().stream().map(this::mapTagesplanToTagesplanResource).collect(Collectors.toList()));
        return resource;
    }

    private TagesplanResource mapTagesplanToTagesplanResource(Tagesplan tagesplan) {
        TagesplanResource resource = new TagesplanResource();
        resource.setTag(tagesplan.getTag().getValue());
        resource.setWerkstattauftraege(tagesplan.getWerkstatttermine().stream().map(this::mapWerkstattauftragToWerkstattauftragResource).collect(Collectors.toList()));
        return resource;
    }

    private WerkstattterminResource mapWerkstattauftragToWerkstattauftragResource(Werkstatttermin werkstatttermin) {
        WerkstattterminResource resource = new WerkstattterminResource();
        if (werkstatttermin.getStart() != null && werkstatttermin.getEnde() != null) {
            resource.setStart(werkstatttermin.getStart().getValue());
            resource.setEnde(werkstatttermin.getEnde().getValue());
        }
        resource.setAuftragsnummer(werkstatttermin.getWerkstattauftragRef().getValue());
        mapBearbeiterToBearbeiterResource(werkstatttermin, resource);
        return resource;
    }

    private void mapBearbeiterToBearbeiterResource(Werkstatttermin werkstatttermin, WerkstattterminResource resource) {
        if (werkstatttermin.getBearbeiter() != null) {
            BearbeiterResource bearbeiterResource = new BearbeiterResource();
            bearbeiterResource.setId(werkstatttermin.getBearbeiter().getId().getValue());
            bearbeiterResource.setName(werkstatttermin.getBearbeiter().getName().getValue());
            bearbeiterResource.setVorname(werkstatttermin.getBearbeiter().getVorname().getValue());
            resource.setBearbeiter(bearbeiterResource);
        }
    }

    public Werkstatttermin mapToWerkstatttermin(WerkstattterminResource resource) {
        Bearbeiter bearbeiter = new Bearbeiter(new BearbeiterId(resource.getBearbeiter().getId()),
                new Name(resource.getBearbeiter().getName()), new Vorname(resource.getBearbeiter().getVorname()));
        WerkstattauftragRef werkstattauftragRef = new WerkstattauftragRef(resource.getAuftragsnummer());
        Start start = new Start(resource.getStart());
        Ende ende = new Ende(resource.getEnde());
        if (resource.getTerminId() != null) {
            return new Werkstatttermin(new WerkstattterminId(resource.getTerminId()),
                    werkstattauftragRef, bearbeiter, start, ende);
        }
        return new Werkstatttermin(werkstattauftragRef, bearbeiter, start, ende);
    }
}
