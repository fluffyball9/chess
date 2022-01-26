public class Chess extends JPanel implements MouseListener, KeyListener {
	private static final long serialVersionUID = -7058791324695713647L;
	/**
	 * JFrame for window
	 */
	private final JFrame f = new JFrame("Chess");
	/**
	 * JPanel for window
	 */
	private final JPanel p = new JPanel();
	/**
	 * List of pieces on board
	 */
	private ArrayList<Pieces> pieces = new ArrayList<Pieces>();
	/**
	 * List of images being used
	 */
	private static final HashMap<String, ImageIcon> images = new HashMap<String, ImageIcon>();
	/**
	 * Image of the Chessboard
	 */
	private static final ImageIcon board = new ImageIcon("Chessboard.png");
	private Integer opened = null;
	private static final Integer side_margin = 16;
	private static final Integer top_margin = 35;
	private Pawn promoting = null;
	private Pawn en_passant = null;
	private Pieces check = null;
	private Boolean checkmate = null;
	private boolean blackTurn = false;

	public Chess() {
		setup();
		f.setVisible(true);
		f.setSize(480 + side_margin, 480 + top_margin);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.setLayout(new java.awt.GridLayout(0, 1));
		p.add(this);
		f.add(p);
		f.setResizable(false);
		f.addMouseListener(this);
		f.addKeyListener(this);
	}

	private void hideAllMoves() {
		for (Pieces piece : pieces) {
			if (piece.moves_shown) {
				piece.moves_shown = false;
				break;
			}
		}
	}

	private boolean containsCoor(int coor) {
		return !(getPiece(coor) == null);
	}

	private class Pieces {
		private int[][] moves = {};
		private boolean black = false;
		private boolean moves_shown = false;
		private int coordinates;

		protected void move(int move) {
			hideAllMoves();
			if (pieces.contains(getPiece(move))) {
				pieces.remove(getPiece(move));
			}
			if (this instanceof King) {
				((King) this).moved = true;
			} else if (this instanceof Rook) {
				((Rook) this).moved = true;
			}
			this.coordinates = move;
		}

		@Override
		public String toString() {
			String color = "White_";
			if (black) {
				color = "Black_";
			}
			return color + this.getClass().getSimpleName();
		}
	}

	private class Pawn extends Pieces {
		private boolean atBeginningPos = true;
		private final static int promoteToKnight = 3;
		private static final int promoteToRook = 2;
		private static final int promoteToBishop = 1;
		private static final int promoteToQueen = 0;

		private Pawn(boolean black, int coordinates) {
			super.black = black;
			super.coordinates = coordinates;
		}

		@Override
		protected void move(int xy) {
			hideAllMoves();
			int current = super.coordinates;
			int difference = xy - super.coordinates;
			if (getPiece(xy) != null && getPiece(current).black == getPiece(xy).black) {
				return;
			}
			int mod = -1;
			if (super.black) {
				mod = 1;
			}
			if (difference == mod && !containsCoor(xy)) {
				super.coordinates = xy;
				this.atBeginningPos = false;
				opened = null;
			}
			if (containsCoor(xy) && getPiece(xy).black != super.black && Math.abs(difference) == 10 - mod
					|| Math.abs(difference) == 10 + mod) {
				pieces.remove(getPiece(xy));
				super.coordinates = xy;
				opened = null;
				this.atBeginningPos = false;
			}
			if (containsCoor(xy - mod) && getPiece(xy - mod) instanceof Pawn
					&& ((Pawn) getPiece(xy - mod)) == en_passant && Math.abs(difference) == 10 - mod
					|| Math.abs(difference) == 10 + mod) {
				pieces.remove(getPiece(xy - mod));
				super.coordinates = xy;
				opened = null;
				this.atBeginningPos = false;
			}
			en_passant = null;
			if ((!containsCoor(xy)) && this.atBeginningPos && difference == mod * 2) {
				this.atBeginningPos = false;
				en_passant = this;
				super.coordinates = xy;
				opened = null;
			}
			if (super.coordinates != xy) {
				super.moves_shown = true;
			} else {
				blackTurn = !blackTurn;
			}
			if (super.coordinates % 10 == 1 || super.coordinates % 10 == 8) {
				promoting = this;
			}
			Chess.this.repaint();
		}

		private void promote(int promotion) {
			if (promotion == promoteToQueen) {
				pieces.add(new Queen(super.black, super.coordinates));
				pieces.remove(this);
			} else if (promotion == promoteToBishop) {
				pieces.add(new Bishop(super.black, super.coordinates));
				pieces.remove(this);
			} else if (promotion == promoteToRook) {
				pieces.add(new Rook(super.black, super.coordinates));
				pieces.remove(this);
			} else if (promotion == promoteToKnight) {
				pieces.add(new Knight(super.black, super.coordinates));
				pieces.remove(this);
			}
		}
	}

	private class Knight extends Pieces {
		private Knight(boolean black, int coordinates) {
			super.black = black;
			super.moves = new int[][] { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 }, { -1, 2 },
					{ -1, -2 } };
			super.coordinates = coordinates;
		}
	}

	private class Rook extends Pieces {
		boolean moved = false;

		private Rook(boolean black, int coordinates) {
			super.black = black;
			super.moves = new int[28][2];
			for (int i = 0; i < 7; i++) {
				super.moves[i][0] = i + 1;
				super.moves[i][1] = 0;
				super.moves[i + 7][0] = -(i + 1);
				super.moves[i + 7][1] = 0;
				super.moves[i + 14][1] = i + 1;
				super.moves[i + 14][0] = 0;
				super.moves[i + 21][1] = -(i + 1);
				super.moves[i + 21][0] = 0;
			}
			super.coordinates = coordinates;
		}
	}

	private class Bishop extends Pieces {
		private Bishop(boolean black, int coordinates) {
			super.black = black;
			super.moves = new int[28][2];
			for (int i = 0; i < 7; i++) {
				super.moves[i][0] = i + 1;
				super.moves[i][1] = i + 1;
				super.moves[i + 7][0] = -(i + 1);
				super.moves[i + 7][1] = -(i + 1);
				super.moves[i + 14][1] = i + 1;
				super.moves[i + 14][0] = i + 1;
				super.moves[i + 21][1] = -(i + 1);
				super.moves[i + 21][0] = -(i + 1);
			}
			super.coordinates = coordinates;
		}
	}

	private class King extends Pieces {
		boolean moved = false;

		private King(boolean black, int coordinates) {
			super.black = black;
			super.moves = new int[][] { { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { 1, 0 }, { -1, 0 }, { -1, 1 },
					{ -1, -1 } };
			super.coordinates = coordinates;
		}

		private void castle() {
			if (this.moved == false && containsCoor(super.coordinates + 30)
					&& getPiece(super.coordinates + 30) instanceof Rook
					&& ((Rook) getPiece(super.coordinates + 30)).moved == false && !containsCoor(super.coordinates + 10)
					&& !containsCoor(super.coordinates + 20)) {
				getPiece(super.coordinates + 30).coordinates -= 20;
				((Rook) getPiece(super.coordinates + 10)).moved = true;
				super.coordinates += 20;
				this.moved = true;
				opened = null;
				super.moves_shown = false;
				blackTurn = !blackTurn;
			}
		}

		private void qcastle() {
			if (this.moved == false && containsCoor(super.coordinates - 40)
					&& getPiece(super.coordinates - 40) instanceof Rook
					&& ((Rook) getPiece(super.coordinates - 40)).moved == false && !containsCoor(super.coordinates - 10)
					&& !containsCoor(super.coordinates - 20) && !containsCoor(super.coordinates - 30)) {
				getPiece(super.coordinates - 40).coordinates += 30;
				((Rook) getPiece(super.coordinates - 10)).moved = true;
				super.coordinates -= 20;
				this.moved = true;
				opened = null;
				super.moves_shown = false;
				blackTurn = !blackTurn;
			}
		}
	}

	private class Queen extends Pieces {
		private Queen(boolean black, int coordinates) {
			super.black = black;
			super.moves = new int[56][2];
			int i = 0;
			for (; i < 7; i++) {
				super.moves[i][0] = i + 1;
				super.moves[i][1] = i + 1;
				super.moves[i + 7][0] = -(i + 1);
				super.moves[i + 7][1] = -(i + 1);
				super.moves[i + 14][1] = i + 1;
				super.moves[i + 14][0] = i + 1;
				super.moves[i + 21][1] = -(i + 1);
				super.moves[i + 21][0] = -(i + 1);
			}
			int itrPlus = i;
			for (; i < 7 + itrPlus; i++) {
				super.moves[i][0] = i + 1;
				super.moves[i][1] = 0;
				super.moves[i + 7][0] = -(i + 1);
				super.moves[i + 7][1] = 0;
				super.moves[i + 14][1] = i + 1;
				super.moves[i + 14][0] = 0;
				super.moves[i + 21][1] = -(i + 1);
				super.moves[i + 21][0] = 0;
			}
			super.coordinates = coordinates;
		}
	}

	/**
	 * Creates a new Chessboard
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new Chess();
	}

	private List<int[]> getPawnMoves(Pieces piece) {
		int mod = -1;
		if (piece.black) {
			mod = 1;
		}
		int coordinates = piece.coordinates + mod;
		List<int[]> list = new ArrayList<int[]>();
		if (!containsCoor(coordinates)) {
			list.add(new int[] { getXCoor(coordinates), getYCoor(coordinates) });
		}
		coordinates += mod;
		if ((!containsCoor(coordinates)) && ((Pawn) piece).atBeginningPos) {
			list.add(new int[] { getXCoor(coordinates), getYCoor(coordinates) });
		}
		coordinates += 10 - mod;
		if (containsCoor(coordinates) && getPiece(coordinates).black != piece.black) {
			list.add(new int[] { getXCoor(coordinates), getYCoor(coordinates) });
		} else if (containsCoor(coordinates - mod) && getPiece(coordinates - mod) instanceof Pawn
				&& ((Pawn) getPiece(coordinates - mod)) == en_passant) {
			list.add(new int[] { getXCoor(coordinates), getYCoor(coordinates) });
		}
		coordinates -= 20;
		if (containsCoor(coordinates) && getPiece(coordinates).black != piece.black) {
			list.add(new int[] { getXCoor(coordinates), getYCoor(coordinates) });
		} else if (containsCoor(coordinates - mod) && getPiece(coordinates - mod) instanceof Pawn
				&& ((Pawn) getPiece(coordinates - mod)) == en_passant) {
			list.add(new int[] { getXCoor(coordinates), getYCoor(coordinates) });
		}
		return list;
	}

	private void drawRect(Graphics g, int x, int y, int width, int height, int thickness) {
		for (int i = 0; i < thickness; i++) {
			g.drawRect(x, y, width, height);
			x++;
			y++;
			width -= 2;
			height -= 2;
		}
	}

	private int getXCoor(int xy) {
		return (Math.floorDiv(xy, 10) - 1) * 60;
	}

	private int getYCoor(int xy) {
		return (Integer.parseInt(String.valueOf(xy).substring(1)) - 1) * 60;
	}

	private Pieces getPiece(int xy) {
		for (Pieces check : pieces) {
			if (check.coordinates == xy) {
				return check;
			}
		}
		return null;
	}

	private void draw(Graphics g) {
		board.paintIcon(this, g, 0, 0);
		for (Pieces piece : pieces) {
			images.get(piece.toString()).paintIcon(this, g, getXCoor(piece.coordinates), getYCoor(piece.coordinates));
		}
		show_moves(g);
		if (promoting != null) {
			g.setColor(Color.GRAY);
			g.fillRoundRect(95, 95, 290, 290, 25, 25);
			g.setColor(Color.WHITE);
			g.fillRect(140, 170, 200, 30);
			g.fillRect(140, 220, 200, 30);
			g.fillRect(140, 270, 200, 30);
			g.fillRect(140, 320, 200, 30);
			g.setFont(new java.awt.Font("TimesRoman", java.awt.Font.BOLD, 20));
			g.drawString("What piece do you want to", 130, 120);
			g.drawString("promote your pawn to?", 140, 140);
			g.setColor(Color.BLACK);
			g.drawString("Queen", 210, 190);
			g.drawString("Bishop", 210, 240);
			g.drawString("Rook", 210, 290);
			g.drawString("Knight", 210, 340);
		}
	}

	private List<int[]> getMoves(Pieces piece) {
		int get = piece.coordinates;
		List<int[]> list;
		if (piece instanceof King) {
			list = getKingSpecialMoves(piece);
		} else if (piece instanceof Knight) {
			list = getKnightMoves(piece);
		} else if (piece instanceof Queen) {
			list = getLineMoves(get, true);
			list.addAll(getLineMoves(get, false));
		} else if (piece instanceof Pawn) {
			list = getPawnMoves(piece);
		} else if (!(piece instanceof Bishop || piece instanceof Rook)) {
			return null;
		} else {
			list = getLineMoves(get, piece instanceof Bishop);
		}

		List<int[]> newList = new ArrayList<int[]>();
		for (int[] current : list) {
			if (current[0] >= 0 && current[1] >= 0 && current[0] <= 420 && current[1] <= 420) {
				newList.add(current);
			}
		}
		list.clear();
		if (check != null && check.black == piece.black) {
			for (int[] current : newList) {
				Pieces temp = null;
				int origCoor = piece.coordinates;
				if (containsCoor((current[0] / 60 + 1) * 10 + (current[1] / 60 + 1))) {
					temp = getPiece((current[0] / 60 + 1) * 10 + (current[1] / 60 + 1));
					pieces.remove(getPiece((current[0] / 60 + 1) * 10 + (current[1] / 60 + 1)));
				}
				piece.coordinates = (current[0] / 60 + 1) * 10 + (current[1] / 60 + 1);
				if (!checkCheck(check.coordinates, check.black)) {
					list.add(current);
				}
				piece.coordinates = origCoor;
				if (temp != null) {
					pieces.add(temp);
				}
			}
		} else {
			Pieces king = null;
			for (Pieces current : pieces) {
				if (current instanceof King && current.black == piece.black) {
					king = current;
					break;
				}
			}
			for (int[] current : newList) {
				Pieces temp = null;
				int origCoor = piece.coordinates;
				if (containsCoor((current[0] / 60 + 1) * 10 + (current[1] / 60 + 1))) {
					temp = getPiece((current[0] / 60 + 1) * 10 + (current[1] / 60 + 1));
					pieces.remove(getPiece((current[0] / 60 + 1) * 10 + (current[1] / 60 + 1)));
				}
				piece.coordinates = (current[0] / 60 + 1) * 10 + (current[1] / 60 + 1);
				if (!checkCheck(king.coordinates, king.black)) {
					list.add(current);
				}
				piece.coordinates = origCoor;
				if (temp != null) {
					pieces.add(temp);
				}
			}
		}
		return list;
	}

	private List<int[]> getMovesNoCheck(Pieces piece) {
		int get = piece.coordinates;
		List<int[]> list;
		if (piece instanceof King) {
			list = getKingSpecialMoves(piece);
		} else if (piece instanceof Knight) {
			list = getKnightMoves(piece);
		} else if (piece instanceof Queen) {
			list = getLineMoves(get, true);
			list.addAll(getLineMoves(get, false));
		} else if (piece instanceof Pawn) {
			list = getPawnMoves(piece);
		} else if (!(piece instanceof Bishop || piece instanceof Rook)) {
			return null;
		} else {
			list = getLineMoves(get, piece instanceof Bishop);
		}

		List<int[]> newList = new ArrayList<int[]>();
		for (int[] current : list) {
			if (current[0] >= 0 && current[1] >= 0 && current[0] <= 420 && current[1] <= 420) {
				newList.add(current);
			}
		}
		return newList;
	}

	private boolean checkCheck(final int kingCoor, boolean black) {
		List<int[]> list = new ArrayList<int[]>();
		for (Pieces current : pieces) {
			if (!(current instanceof King || current.black == black)) {
				list.addAll(getMovesNoCheck(current));
			}
		}
		for (int[] xy : list) {
			if (xy[0] == getXCoor(kingCoor) && xy[1] == getYCoor(kingCoor)) {
				return true;
			}
		}
		return false;
	}

	private List<int[]> getKingSpecialMoves(Pieces piece) {
		List<int[]> list = new ArrayList<int[]>();
		final int coordinates = piece.coordinates;
		for (int i = 0; i < piece.moves.length; i++) {
			int[] temp = piece.moves[i];
			int newCoor = coordinates + (temp[0]) * 10 + temp[1];
			if (newCoor <= 9) {
				continue;
			}
			int x = getXCoor(newCoor);
			int y = getYCoor(newCoor);
			if (containsCoor(newCoor) && getPiece(newCoor).black == piece.black)
				;
			else {
				list.add(new int[] { x, y });
			}
		}
		if (((King) piece).moved == false && containsCoor(coordinates + 30)
				&& getPiece(coordinates + 30) instanceof Rook && ((Rook) getPiece(coordinates + 30)).moved == false
				&& !containsCoor(coordinates + 10) && !containsCoor(coordinates + 20)) {
			list.add(new int[] { getXCoor(coordinates + 20), getYCoor(coordinates) });
		}
		if (((King) piece).moved == false && containsCoor(coordinates - 40)
				&& getPiece(coordinates - 40) instanceof Rook && ((Rook) getPiece(coordinates - 40)).moved == false
				&& !containsCoor(coordinates - 10) && !containsCoor(coordinates - 20)
				&& !containsCoor(coordinates - 30)) {
			list.add(new int[] { getXCoor(coordinates - 20), getYCoor(coordinates) });
		}
		return list;
	}

	private List<int[]> getLineMoves(int xy, boolean modify) {
		final int x = getXCoor(xy);
		final int y = getYCoor(xy);
		boolean movesFound = false;
		List<int[]> list = new ArrayList<int[]>();
		boolean[] side = { true, true, true, true };
		int[][] current = { { x, y }, { x, y }, { x, y }, { x, y } };
		if (modify) {
			for (int i = 0; i < 8; i++) {
				current[0][1] -= 60;
				current[0][0] -= 60;
				int otherX = current[0][0];
				int otherY = current[0][1];
				if (((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black)
						|| !side[0])) {
					side[0] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[0] = false;
					}
				}
				current[1][1] += 60;
				current[1][0] += 60;
				otherX = current[1][0];
				otherY = current[1][1];
				if ((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black
						|| !side[1])) {
					side[1] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[1] = false;
					}
				}
				current[2][0] -= 60;
				current[2][1] += 60;
				otherX = current[2][0];
				otherY = current[2][1];
				if ((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black
						|| !side[2])) {
					side[2] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[2] = false;
					}
				}
				current[3][0] += 60;
				current[3][1] -= 60;
				otherX = current[3][0];
				otherY = current[3][1];
				if ((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black
						|| !side[3])) {
					side[3] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[3] = false;
					}
				}
			}
		} else {
			for (int i = 0; i < 8; i++) {
				current[0][1] -= 60;
				int otherX = current[0][0];
				int otherY = current[0][1];
				if ((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black
						|| !side[0])) {
					side[0] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[0] = false;
					}
				}
				current[1][1] += 60;
				otherX = current[1][0];
				otherY = current[1][1];
				if ((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black
						|| !side[1])) {
					side[1] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[1] = false;
					}
				}
				current[2][0] -= 60;
				otherX = current[2][0];
				otherY = current[2][1];
				if ((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black
						|| !side[2])) {
					side[2] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[2] = false;
					}
				}
				current[3][0] += 60;
				otherX = current[3][0];
				otherY = current[3][1];
				if ((containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
						&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black == getPiece(xy).black
						|| !side[3])) {
					side[3] = false;
				} else {
					movesFound = true;
					list.add(new int[] { otherX, otherY });
					if (containsCoor((otherX / 60 + 1) * 10 + (otherY / 60 + 1))
							&& getPiece((otherX / 60 + 1) * 10 + (otherY / 60 + 1)).black != getPiece(xy).black) {
						side[3] = false;
					}
				}
			}
		}
		if (!movesFound) {
			getPiece(xy).moves_shown = false;
		}
		return list;
	}

	private void show_moves(Graphics g) {
		for (Pieces piece : pieces) {
			if (piece.moves_shown) {
				g.setColor(new Color(0, 128, 0));
				List<int[]> list = getMoves(piece);
				for (int[] current : list) {
					drawRect(g, current[0], current[1], 60, 60, 5);
				}
				if (list.isEmpty()) {
					piece.moves_shown = false;
					opened = null;
				}
				break;
			}
		}
		if (check != null) {
			g.setColor(new Color(128, 0, 0));
			drawRect(g, getXCoor(check.coordinates), getYCoor(check.coordinates), 60, 60, 5);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	/**
	 * Sets up the Chessboard
	 * 
	 * @throws IOException
	 */
	private void setup() {
		pieces.add(new Rook(true, 11));
		pieces.add(new Rook(true, 81));
		pieces.add(new Rook(false, 18));
		pieces.add(new Rook(false, 88));
		pieces.add(new Knight(true, 21));
		pieces.add(new Knight(true, 71));
		pieces.add(new Knight(false, 28));
		pieces.add(new Knight(false, 78));
		pieces.add(new Bishop(true, 31));
		pieces.add(new Bishop(true, 61));
		pieces.add(new Bishop(false, 38));
		pieces.add(new Bishop(false, 68));
		pieces.add(new Queen(true, 41));
		pieces.add(new King(true, 51));
		pieces.add(new Queen(false, 48));
		pieces.add(new King(false, 58));
		for (int i = 1; i <= 8; i++) {
			pieces.add(new Pawn(true, i * 10 + 2));
			pieces.add(new Pawn(false, i * 10 + 7));
		}
		for (Pieces piece : pieces) {
			images.put(piece.toString(), new ImageIcon(piece.toString() + ".png"));
		}
	}

	private List<int[]> getKnightMoves(Pieces piece) {
		List<int[]> list = new ArrayList<int[]>();
		int coordinates = piece.coordinates;
		for (int i = 0; i < piece.moves.length; i++) {
			int[] temp = piece.moves[i];
			int newCoor = coordinates + temp[0] * 10 + temp[1];
			if (newCoor <= 9) {
				continue;
			}
			int x = getXCoor(newCoor);
			int y = getYCoor(newCoor);
			if (containsCoor(newCoor) && getPiece(newCoor).black == piece.black)
				;
			else {
				list.add(new int[] { x, y });
			}
		}
		return list;
	}

	private void proccessMove(int xy, Pieces piece) {
		if (piece instanceof Pawn) {
			((Pawn) piece).move(xy);
		} else if (piece instanceof King && piece.coordinates == xy - 20) {
			((King) piece).castle();
			en_passant = null;
		} else if (piece instanceof King && piece.coordinates == xy + 20) {
			((King) piece).qcastle();
			en_passant = null;
		} else {
			for (int[] current : getMoves(piece)) {
				if (current[0] == getXCoor(xy) && current[1] == getYCoor(xy)) {
					piece.move(xy);
					opened = null;
					blackTurn = !blackTurn;
					en_passant = null;
					break;
				}
			}
		}
		if (piece.coordinates == xy) {
			for (Pieces current : pieces) {
				if (current instanceof King && piece.black != current.black) {
					if (checkCheck(current.coordinates, current.black)) {
						check = current;
						boolean checkmateThis = true;
						List<int[]> list = getMoves(current);
						for (int[] check : list) {
							if (!checkCheck((check[0] / 60 + 1) * 10 + (check[1] / 60 + 1), current.black)) {
								checkmateThis = false;
								break;
							}
						}
						for (Pieces check : new ArrayList<Pieces>(pieces)) {
							if ((!getMoves(check).isEmpty()) && check.black == this.check.black) {
								checkmateThis = false;
								break;
							}
						}
						if (checkmateThis) {
							checkmate = current.black;
						}
					} else {
						check = null;
					}
					break;
				}
			}
		}
		this.repaint();
		if (checkmate != null) {
			UIManager.put("OptionPane.minimumSize", new Dimension(400, 200));
			String color;
			if (checkmate) {
				color = "White";
			} else {
				color = "Black";
			}
			int res = JOptionPane.showConfirmDialog(null, color + " wins. Do you want to play again?", "Checkmate",
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (res == 0) {
				pieces.clear();
				en_passant = promoting = null;
				checkmate = null;
				check = null;
				blackTurn = false;
				setup();
				this.repaint();
			} else {
				System.exit(0);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			final int x = e.getX() - 6;
			final int y = e.getY() - 29;
			if (promoting != null) {
				if (x >= 140 && x <= 340) {
					if (y >= 170 && y <= 200) {
						promoting.promote(Pawn.promoteToQueen);
						promoting = null;
					} else if (y >= 220 && y <= 250) {
						promoting.promote(Pawn.promoteToBishop);
						promoting = null;
					} else if (y >= 270 && y <= 300) {
						promoting.promote(Pawn.promoteToRook);
						promoting = null;
					} else if (y >= 320 && y <= 350) {
						promoting.promote(Pawn.promoteToKnight);
						promoting = null;
					}
				}
				this.repaint();
				return;
			}
			int xy = (Math.floorDiv(x, 60) + 1) * 10 + (Math.floorDiv(y, 60) + 1);
			Pieces piece = getPiece(xy);
			if (opened != null) {
				if (piece == null) {
					proccessMove(xy, getPiece(opened));
					return;
				} else if (opened == xy) {
					piece.moves_shown = false;
					opened = null;
				} else {
					proccessMove(xy, getPiece(opened));
				}
			} else if (piece == null || piece.black != blackTurn) {
				return;
			} else {
				hideAllMoves();
				piece.moves_shown = true;
				opened = xy;
			}
			this.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}
}