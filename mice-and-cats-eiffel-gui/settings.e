note
	description: "Summary description for {SETTINGS}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	SETTINGS

feature

	game_board_width: INTEGER = 20
	game_board_height: INTEGER = 15

	number_of_subways: INTEGER = 5
	max_subway_segments: INTEGER = 5
	min_subway_segment_length: INTEGER = 3
	max_subway_segment_length: INTEGER = 5

end
