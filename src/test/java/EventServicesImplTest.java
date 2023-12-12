import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testAddParticipant() {
        Participant participant = new Participant();
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant savedParticipant = eventServices.addParticipant(participant);

        assertNotNull(savedParticipant);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    public void testAddAffectEvenParticipant_WithValidEventAndParticipant() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setIdPart(1);
        event.setParticipants(new HashSet<>(Collections.singletonList(participant)));

        when(eventRepository.save(event)).thenReturn(event);
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));

        Event savedEvent = eventServices.addAffectEvenParticipant(event, 1);

        assertNotNull(savedEvent);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testAddAffectEvenParticipant_WithValidEvent() {
        Event event = new Event();
        Set<Participant> participants = new HashSet<>();
        Participant participant1 = new Participant();
        participant1.setIdPart(1);
        Participant participant2 = new Participant();
        participant2.setIdPart(2);
        participants.add(participant1);
        participants.add(participant2);
        event.setParticipants(participants);

        when(participantRepository.findById(participant1.getIdPart())).thenReturn(Optional.of(participant1));
        when(participantRepository.findById(participant2.getIdPart())).thenReturn(Optional.of(participant2));
        when(eventRepository.save(event)).thenReturn(event);

        Event savedEvent = eventServices.addAffectEvenParticipant(event);

        assertNotNull(savedEvent);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testAddAffectLog_WithValidLogisticsAndEventDescription() {
        Logistics logistics = new Logistics();
        logistics.setIdLog(1);
        String eventDescription = "Sample Event Description";

        Event event = new Event();
        event.setDescription(eventDescription);

        when(eventRepository.findByDescription(eventDescription)).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics savedLogistics = eventServices.addAffectLog(logistics, eventDescription);

        assertNotNull(savedLogistics);
        verify(logisticsRepository, times(1)).save(logistics);
    }


 @Test
    public void testGetLogisticsDates_WithValidDateRange() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        Event mockEvent1 = new Event();
        mockEvent1.setIdEvent(1);
        mockEvent1.setDateDebut(LocalDate.of(2023, 3, 15));
        Logistics logistics1 = new Logistics();
        logistics1.setIdLog(1);
        logistics1.setReserve(true);
        Logistics logistics2 = new Logistics();
        logistics2.setIdLog(2);
        logistics2.setReserve(false);
        mockEvent1.setLogistics(new HashSet<>(Arrays.asList(logistics1, logistics2)));

        Event mockEvent2 = new Event();
        mockEvent2.setIdEvent(2);
        mockEvent2.setDateDebut(LocalDate.of(2023, 6, 10));
        Logistics logistics3 = new Logistics();
        logistics3.setIdLog(3);
        logistics3.setReserve(true);
        mockEvent2.setLogistics(Collections.singleton(logistics3));

        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(Arrays.asList(mockEvent1, mockEvent2));

        List<Logistics> resultLogistics = eventServices.getLogisticsDates(startDate, endDate);

        assertNotNull(resultLogistics);
        assertEquals(3, resultLogistics.size());
        assertTrue(resultLogistics.contains(logistics1));
        assertTrue(resultLogistics.contains(logistics2));
        assertTrue(resultLogistics.contains(logistics3));
    }
    @Test
    public void testCalculCout() {
        // Création d'un événement simulé avec des logistiques
        Event mockEvent = new Event();
        mockEvent.setIdEvent(1);
        mockEvent.setDescription("Description de l'événement");

        Logistics logistics1 = new Logistics();
        logistics1.setIdLog(1);
        logistics1.setReserve(true);
        logistics1.setPrixUnit(10f);
        logistics1.setQuantite(5);

        Logistics logistics2 = new Logistics();
        logistics2.setIdLog(2);
        logistics2.setReserve(false); // Non réservé, ne devrait pas être inclus dans le calcul

        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics1);
        logisticsSet.add(logistics2);

        mockEvent.setLogistics(logisticsSet);

        // Définition du comportement simulé pour eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR))
                .thenReturn(Collections.singletonList(mockEvent));

        // Appel de la méthode à tester
        eventServices.calculCout();

        // Vérification du calcul du coût
        assertEquals(50f, mockEvent.getCout()); // (10 * 5) = 50
        verify(eventRepository, times(1)).save(mockEvent);
    }
}



