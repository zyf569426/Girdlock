public class Car {
        /*
         * type 0 means the player car
         * type 1 means small car can go up&down
         * type 2 means small car can go left&right
         * type 3 means big car can go up&down
         * type 4 means big car can go left&right
         */
        private int type;
        private int width;
        private int length;
        private int posX;
        private int posY;
        
        public Car(int type, int posX, int posY, int width, int length) {
                this.type = type;
                this.width = width;
                this.length = length;
                this.posX = posX;
                this.posY= posY;
        }

        /**
         * @return the type
         */
        public int getType() {
                return type;
        }

        /**
         * @return the width
         */
        public int getWidth() {
                return width;
        }

        /**
         * @return the length
         */
        public int getLength() {
                return length;
        }

        /**
         * @return the posX
         */
        public int getPosX() {
                return posX;
        }

        /**
         * @param posX the posX to set
         */
        public void setPosX(int posX) {
                this.posX = posX;
        }

        /**
         * @return the posY
         */
        public int getPosY() {
                return posY;
        }

        /**
         * @param posY the posY to set
         */
        public void setPosY(int posY) {
                this.posY = posY;
        }
        
}
