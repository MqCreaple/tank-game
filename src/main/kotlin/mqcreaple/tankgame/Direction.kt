package mqcreaple.tankgame

import mqcreaple.tankgame.controller.Controller

enum class Direction {
    UP {
        override val x: Int
            get() = 0
        override val y: Int
            get() = 1
        override val angle: Double
            get() = 0.0
    }, DOWN {
        override val x: Int
            get() = 0
        override val y: Int
            get() = -1
        override val angle: Double
            get() = 180.0
    }, LEFT {
        override val x: Int
            get() = -1
        override val y: Int
            get() = 0
        override val angle: Double
            get() = 90.0
    }, RIGHT {
        override val x: Int
            get() = 1
        override val y: Int
            get() = 0
        override val angle: Double
            get() = 270.0
    };
    abstract val x: Int
    abstract val y: Int
    abstract val angle: Double
}