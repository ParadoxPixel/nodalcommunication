package nl.iobyte.nodalcommunication.rabbitmq;

import nl.iobyte.rabbitwrapper.rabbit.objects.RabbitConfig;

public class RabbitSourceConfig {

    private final int maxSend, maxConsume;

    public RabbitSourceConfig(int maxSend, int maxConsume) {
        this.maxSend = maxSend;
        this.maxConsume = maxConsume;
    }

    public int getMaxSend() {
        return maxSend;
    }

    public int getMaxConsume() {
        return maxConsume;
    }

    /**
     * RabbitConfig from RabbitSourceConfig
     * @return RabbitConfig
     */
    public RabbitConfig toRabbit() {
        return new RabbitConfig(
                maxSend,
                0,
                maxConsume
        );
    }

}
