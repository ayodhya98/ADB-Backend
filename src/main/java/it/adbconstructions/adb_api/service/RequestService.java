package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.AlreadyActiveRequestFoundException;
import it.adbconstructions.adb_api.exception.AlreadyActiveRoomFoundException;
import it.adbconstructions.adb_api.exception.RequestNotFoundException;
import it.adbconstructions.adb_api.model.Consumer;
import it.adbconstructions.adb_api.model.Request;
import it.adbconstructions.adb_api.model.Room;
import it.adbconstructions.adb_api.model.User;
import it.adbconstructions.adb_api.repository.ConsumerRepository;
import it.adbconstructions.adb_api.repository.RequestRepository;
import it.adbconstructions.adb_api.repository.RoomRepository;
import it.adbconstructions.adb_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static it.adbconstructions.adb_api.common.constant.ErrorUtil.*;

@Service
@Transactional
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ChatRoomService chatRoomService;

    public List<Request> getRequests()
    {
        return requestRepository.findAll();
    }

    public Request createRequest(String requestedUsername, String content) throws AlreadyActiveRequestFoundException, AlreadyActiveRoomFoundException {
        List<Request> existingRequests = requestRepository.findByConsumerUsername(requestedUsername);
        Room existingRoom = roomRepository.findByConsumerUsername(requestedUsername);
        if (existingRoom == null)
        {
            if (existingRequests == null || existingRequests.size() == 0 )
            {
                Consumer sender = consumerRepository.findUserByUsername(requestedUsername);
                Request newRequest = new Request();
                newRequest.setRequestCode(generateCode(requestedUsername));
                newRequest.setContent(content);
                newRequest.setAccepted(false);
                newRequest.setFinished(false);
                newRequest.setRequestDate(new Date());
                newRequest.setRequestedUser(sender);
                consumerRepository.save(sender);
                return requestRepository.save(newRequest);
            }
            else
            {
                throw new AlreadyActiveRequestFoundException(ACTIVE_REQUEST_ALREADY_EXIST);
            }
        }
        else
        {
            throw new AlreadyActiveRoomFoundException(ACTIVE_ROOM_ALREADY_EXIST);
        }
    }

    public Request assignConsultant(String senderUsername, String assigneeUsername,String requestCode) throws RequestNotFoundException {
        Request request = requestRepository.findByRequestCode(requestCode);
        if (request.getRequestedUser().getUsername().equals(senderUsername))
        {
            User consultant = userRepository.findUserByUsername(assigneeUsername);
            request.setAssignedAdmin(consultant);
            userRepository.save(consultant);
            request.setAccepted(true);
            chatRoomService.createRoom(senderUsername,assigneeUsername);
            return requestRepository.save(request);
        }
        else
        {
            throw new RequestNotFoundException(REQUEST_NOT_FOUND_BY_USERNAME);
        }
    }

    public Request updateStatus(String senderUsername, Boolean isFinished,String requestCode) throws RequestNotFoundException {
        Request request = requestRepository.findByRequestCode(requestCode);
        if (request.getRequestedUser().getUsername().equals(senderUsername))
        {
            request.setFinished(isFinished);
            return requestRepository.save(request);
        }
        else
        {
            throw new RequestNotFoundException(REQUEST_NOT_FOUND_BY_USERNAME);
        }
    }

    public void deleteRequest(String senderUsername,String requestCode) throws AlreadyActiveRequestFoundException, RequestNotFoundException {
        Request request = requestRepository.findByRequestCode(requestCode);
        if (request != null && request.getRequestedUser().getUsername().equals(senderUsername))
        {
            if (request.getFinished())
            {
                requestRepository.delete(request);
            }
            else
            {
                throw new AlreadyActiveRequestFoundException(CAN_NOT_DELETE_ACTIVE_REQUEST);
            }
        }
        else
        {
            throw new RequestNotFoundException(REQUEST_NOT_FOUND_BY_USERNAME);
        }
    }

    public String generateCode(String inputString) {
        Random random = new Random();
        int randomNumber = random.nextInt(90000) + 10000; // generate 5-digit number
        String prefix = inputString.substring(0, 2); // get first 2 characters of input string
        String code = prefix + randomNumber; // concatenate prefix and random number
        return code;
    }

    public Boolean getStatus(String username){
        List<Request> requestList = requestRepository.findByRequestedUserUsername(username);
        if (requestList != null && requestList.size() > 0)
        {
            return requestList.get(0).getAccepted();
        }
        else
        {
            return false;
        }
    }

    public List<Request> getRequestsByAdmin()
    {
        return requestRepository.findAll();
    }
}
