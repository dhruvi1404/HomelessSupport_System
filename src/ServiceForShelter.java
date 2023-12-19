public class ServiceForShelter {
    private int shelterId;
    private int serviceId;

    public ServiceForShelter(int shelterId, int serviceId) {
        this.shelterId = shelterId;
        this.serviceId = serviceId;
    }

    public int getShelterId() {
        return shelterId;
    }

    public void setShelterId(int shelterId) {
        this.shelterId = shelterId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
}
