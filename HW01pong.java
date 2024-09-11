class HW01pong extends Cow {

	// inspired by 7-segment clock displays. built by seven congruent rectangles (I hope).
	static final int top = 0, topLeft = 1, topRight = 2, middle = 3, bottomLeft = 4, bottomRight = 5, bottom = 6; 
	static void drawSegment(int segment, double x, double y, Color color) {
		switch(segment) {
		case top:
			drawRectangle(x - 5, y + 10, x + 5, y + 13, color);
			break;
		case topLeft:
			drawRectangle(x - 8, y - 0, x - 5, y + 10, color);
			break;
		case topRight:
			drawRectangle(x + 8, y - 0, x + 5, y + 10, color);
			break;
		case middle:
			drawRectangle(x - 5, y, x + 5, y - 3, color);
			break;
		case bottomLeft:
			drawRectangle(x - 8, y - 3, x - 5, y - 13, color);
			break;
		case bottomRight:
			drawRectangle(x + 8, y - 3, x + 5, y - 13, color);
			break;
		case bottom:
			drawRectangle(x - 5, y - 13, x + 5, y - 16, color);
			break;
		}
	}

	static final boolean[][] digitSegments = {
			{true,  true,  true,  false, true,  true,  true},  // 0
			{false, false, true,  false, false, true,  false}, // 1
			{true,  false, true,  true,  true,  false, true},  // 2
			{true,  false, true,  true,  false, true,  true},  // 3
			{false, true,  true,  true,  false, true,  false}, // 4
			{true,  true,  false, true,  false, true,  true},  // 5
			{true,  true,  false, true,  true,  true,  true},  // 6
			{true,  false, true,  false, false, true,  false}, // 7
			{true,  true,  true,  true,  true,  true,  true},  // 8
			{true,  true,  true,  true,  false, true,  true}   // 9
	};

	static void drawDigit(int digit, double x, double y) {
		for(int segment = 0; segment < 7; segment++) {
			if(digitSegments[digit][segment]) {
				drawSegment(segment, x, y, WHITE);
			}
		}
	}
	
	static void resetBall(double[] ballPosition, double[] ballVelocity, boolean serveToRight) {
		ballPosition[0] = 128.0;
		ballPosition[1] = Math.random() * 128.0 + 64.0; // random y, not all the way at 256 or 0 because that would be mean 
		double speed = Math.random() * 1.5 + 3.0; // random speed (3.0, 4.5)
		double angle = Math.random() * Math.PI / 2 - Math.PI / 4; // random angle between -45 and 45 degrees
		ballVelocity[0] = speed * Math.cos(angle) * (serveToRight ? 1 : -1);
		ballVelocity[1] = speed * Math.sin(angle);
	}

	public static void main(String[] arguments) {
		canvasConfig(0.0, 0.0, 256.0, 256.0, BLACK);
		double leftPaddleY = 128.0;
		double rightPaddleY = 128.0;
		double paddleWidth = 7.0;
		double paddleHeight = 40.0;
		double paddleSpeed = 5.0;

		double ballSize = 4.0;
		double[] ballPosition = {128.0, 128.0};
		double[] ballVelocity = {5.0, 0.0};

		int leftScore = 0;
		int rightScore = 0;

		boolean serveToRight = true; // who gets the ball?

				resetBall(ballPosition, ballVelocity, serveToRight);

				while(beginFrame()) {
					// middle line
					for(int i = 0; i < 256; i+= 9) {
						drawRectangle(127, i, 129, i+5, WHITE);
					}

					// paddles
					drawRectangle(0, leftPaddleY - paddleHeight / 2, paddleWidth, leftPaddleY + paddleHeight / 2, WHITE);
					drawRectangle(256 - paddleWidth, rightPaddleY - paddleHeight / 2, 256, rightPaddleY + paddleHeight / 2, WHITE);

					// ball
					drawCircle(ballPosition[0], ballPosition[1], ballSize, WHITE);

					// score
					drawDigit(leftScore, 100, 230); 
					drawDigit(rightScore, 150, 230);

					// moving parts
					if(keyHeld('W') && leftPaddleY + paddleHeight / 2 < 256) {
						leftPaddleY += paddleSpeed;
					}
					if(keyHeld('S') && leftPaddleY - paddleHeight / 2 > 0) {
						leftPaddleY -= paddleSpeed;
					}

					if(keyHeld(UP_ARROW) && rightPaddleY + paddleHeight / 2 < 256) {
						rightPaddleY += paddleSpeed;
					}
					if(keyHeld(DOWN_ARROW) && rightPaddleY - paddleHeight / 2 > 0) {
						rightPaddleY -= paddleSpeed;
					}

					// ball movement
					ballPosition[0] += ballVelocity[0];
					ballPosition[1] += ballVelocity[1];

					// paddle hit
					if(ballPosition[0] - ballSize / 2 <= paddleWidth && ballPosition[1] >= leftPaddleY - paddleHeight / 2 && ballPosition[1] <= leftPaddleY + paddleHeight / 2) {
						ballVelocity[0] *= -1;
						// ball goes up or down depending on if it hits the top or bottom of paddle
						ballVelocity[1] = (ballPosition[1] - leftPaddleY) / (paddleHeight / 2) * 4;
					} 
					else if(ballPosition[0] + ballSize / 2 >= 256 - paddleWidth && ballPosition[1] >= rightPaddleY - paddleHeight / 2 && ballPosition[1] <= rightPaddleY + paddleHeight / 2) {
						ballVelocity[0] *= -1;
						// ditto
						ballVelocity[1] = (ballPosition[1] - rightPaddleY) / (paddleHeight / 2) * 4;
					}

					// top and bottom collision
					if(ballPosition[1] - ballSize / 2 <= 0 || ballPosition[1] + ballSize / 2 >= 256) {
						ballVelocity[1] *= -1;
					}

					// scoring
					if(ballPosition[0] < 0) {
						rightScore++;
						serveToRight = false;
						resetBall(ballPosition, ballVelocity, serveToRight);
					} 
					else if(ballPosition[0] > 256) {
						leftScore++;
						serveToRight = true;
						resetBall(ballPosition, ballVelocity, serveToRight);
					}
				}
	}
}