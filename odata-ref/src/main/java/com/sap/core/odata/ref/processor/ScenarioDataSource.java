package com.sap.core.odata.ref.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.odata.api.edm.EdmEntitySet;
import com.sap.core.odata.api.edm.EdmFunctionImport;
import com.sap.core.odata.api.exception.ODataException;
import com.sap.core.odata.api.exception.ODataNotFoundException;
import com.sap.core.odata.api.exception.ODataNotImplementedException;
import com.sap.core.odata.ref.model.Building;
import com.sap.core.odata.ref.model.DataContainer;
import com.sap.core.odata.ref.model.Employee;
import com.sap.core.odata.ref.model.Location;
import com.sap.core.odata.ref.model.Manager;
import com.sap.core.odata.ref.model.Photo;
import com.sap.core.odata.ref.model.Room;
import com.sap.core.odata.ref.model.Team;

/**
 * Data for the reference scenario
 * @author SAP AG
 */
public class ScenarioDataSource implements ListsDataSource {

  private static final Logger log = LoggerFactory.getLogger(ScenarioDataSource.class);

  private final DataContainer dataContainer;

  public ScenarioDataSource(final DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  @Override
  public List<?> readData(final EdmEntitySet entitySet) throws ODataException {
    ArrayList<Object> data = new ArrayList<Object>();
    if ("Employees".equals(entitySet.getName()))
      data.addAll(dataContainer.getEmployeeSet());
    else if ("Teams".equals(entitySet.getName()))
      data.addAll(dataContainer.getTeamSet());
    else if ("Rooms".equals(entitySet.getName()))
      data.addAll(dataContainer.getRoomSet());
    else if ("Managers".equals(entitySet.getName()))
      data.addAll(dataContainer.getManagerSet());
    else if ("Buildings".equals(entitySet.getName()))
      data.addAll(dataContainer.getBuildingSet());
    else if ("Photos".equals(entitySet.getName()))
      data.addAll(dataContainer.getPhotoSet());
    else
      throw new ODataNotImplementedException();
    return data;
  }

  @Override
  public Object readData(final EdmEntitySet entitySet, final Map<String, Object> keys) throws ODataException {
    if ("Employees".equals(entitySet.getName())) {
      for (Employee employee : dataContainer.getEmployeeSet())
        if (employee.getId().equals(keys.get("EmployeeId")))
          return employee;
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    } else if ("Teams".equals(entitySet.getName())) {
      for (Team team : dataContainer.getTeamSet())
        if (team.getId().equals(keys.get("Id")))
          return team;
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    } else if ("Rooms".equals(entitySet.getName())) {
      for (Room room : dataContainer.getRoomSet())
        if (room.getId().equals(keys.get("Id")))
          return room;
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    } else if ("Managers".equals(entitySet.getName())) {
      for (Manager manager : dataContainer.getManagerSet())
        if (manager.getId().equals(keys.get("EmployeeId")))
          return manager;
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    } else if ("Buildings".equals(entitySet.getName())) {
      for (Building building : dataContainer.getBuildingSet())
        if (building.getId().equals(keys.get("Id")))
          return building;
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    } else if ("Photos".equals(entitySet.getName())) {
      for (Photo photo : dataContainer.getPhotoSet())
        if (photo.getId() == (Integer) keys.get("Id") && photo.getType().equals(keys.get("Type")))
          return photo;
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    }

    throw new ODataNotImplementedException();
  }

  @Override
  public Object readRelatedData(final EdmEntitySet sourceEntitySet, final Object sourceData, final EdmEntitySet targetEntitySet, final Map<String, Object> targetKeys) throws ODataException {
    if ("Employees".equals(targetEntitySet.getName())) {
      ArrayList<Object> data = new ArrayList<Object>();
      if ("Teams".equals(sourceEntitySet.getName())) {
        for (final Employee employee : dataContainer.getEmployeeSet())
          if (employee.getTeam().getId().equals(((Team) sourceData).getId()))
            data.add(employee);
      } else if ("Rooms".equals(sourceEntitySet.getName())) {
        for (final Employee employee : dataContainer.getEmployeeSet())
          if (employee.getRoom().getId().equals(((Room) sourceData).getId()))
            data.add(employee);
      } else if ("Managers".equals(sourceEntitySet.getName())) {
        for (final Employee employee : dataContainer.getEmployeeSet())
          if (employee.getManager().getId().equals(((Manager) sourceData).getId()))
            data.add(employee);
      }
      if (data.isEmpty())
        throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    }
    return null;
  }

  @Override
  public Object readDataFromFunction(EdmFunctionImport function, Map<String, Object> parameters, Map<String, Object> keys) throws ODataException {
    if (function.getName().equals("EmployeeSearch")) {
      if (parameters.get("q") == null)
        throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      else
        return searchEmployees((String) parameters.get("q"));
    } else if (function.getName().equals("AllLocations")) {
      return getLocations();
    } else if (function.getName().equals("AllUsedRoomIds")) {
      ArrayList<Room> data = new ArrayList<Room>();
      for (Room room : dataContainer.getRoomSet())
        if (!room.getEmployees().isEmpty())
          data.add(room);
      return data;
    } else if (function.getName().equals("MaximalAge")) {
      return getOldestEmployee().getAge();
    } else if (function.getName().equals("MostCommonLocation")) {
      return getMostCommonLocation();
    } else if (function.getName().equals("ManagerPhoto")) {
      if (parameters.get("Id") == null)
        throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      final String id = (String) parameters.get("Id");
      for (Employee potentialManager : dataContainer.getEmployeeSet())
        if (potentialManager.getId().equals(id) && isManager(potentialManager))
          return potentialManager.getImage();
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    } else if (function.getName().equals("OldestEmployee")) {
      return getOldestEmployee();
    } else
      throw new ODataNotImplementedException();
  }

  private List<Employee> searchEmployees(final String search) {
    List<Employee> employees = new ArrayList<Employee>();
    for (Employee employee : dataContainer.getEmployeeSet())
      if (employee.getEmployeeName().contains(search) || employee.getLocation() != null
          && (employee.getLocation().getCity().getCityName().contains(search) || employee.getLocation().getCity().getPostalCode().contains(search) || employee.getLocation().getCountry().contains(search)))
        employees.add(employee);
    return employees;
  }

  private HashMap<Location, Integer> getLocations() {
    HashMap<Location, Integer> locations = new HashMap<Location, Integer>();
    for (Employee employee : dataContainer.getEmployeeSet())
      if (employee.getLocation() != null && employee.getLocation().getCity() != null) {
        boolean found = false;
        for (Location location : locations.keySet())
          if (employee.getLocation().getCity().getPostalCode() == location.getCity().getPostalCode() && employee.getLocation().getCity().getCityName() == location.getCity().getCityName() && employee.getLocation().getCountry() == location.getCountry()) {
            found = true;
            locations.put(location, locations.get(location) + 1);
          }
        if (!found)
          locations.put(employee.getLocation(), 1);
      }
    return locations;
  }

  private Location getMostCommonLocation() {
    Integer count = 0;
    Location location = null;
    for (Entry<Location, Integer> entry : getLocations().entrySet())
      if (entry.getValue() > count) {
        count = entry.getValue();
        location = entry.getKey();
      }
    return location;
  }

  private Employee getOldestEmployee() {
    Employee oldestEmployee = null;
    for (Employee employee : dataContainer.getEmployeeSet())
      if (oldestEmployee == null || employee.getAge() > oldestEmployee.getAge())
        oldestEmployee = employee;
    return oldestEmployee;
  }

  private boolean isManager(final Employee potentialManager) {
    for (Employee employee : dataContainer.getEmployeeSet())
      if (potentialManager.getId().equals(employee.getManager().getId()))
        return true;
    return false;
  }

  @Override
  public Object newDataObject(final EdmEntitySet entitySet) throws ODataException {
    if ("Employees".equals(entitySet.getName()))
      return new Employee();
    else if ("Teams".equals(entitySet.getName()))
      return new Team();
    else if ("Rooms".equals(entitySet.getName()))
      return new Room();
    else if ("Managers".equals(entitySet.getName()))
      return new Manager();
    else if ("Buildings".equals(entitySet.getName()))
      return new Building();
    else if ("Photos".equals(entitySet.getName()))
      return new Photo();
    else
      throw new ODataNotImplementedException();
  }

}
