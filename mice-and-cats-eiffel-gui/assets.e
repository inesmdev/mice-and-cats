note
	description: "This class provides access to the games resources"
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	ASSETS

create
	load_from_directory

feature -- constants
	cat_id: INTEGER
		once Result := 1 end
	mouse_id: INTEGER
		once Result := 2 end

feature {NONE}

	images: ARRAYED_LIST [EV_PIXMAP]
	stretched_images_lru: ARRAYED_LIST [EV_PIXMAP]

	load_from_directory (directory: READABLE_STRING_GENERAL)
		local
			image: EV_PIXMAP
		do
			create images.make (2)
			create stretched_images_lru.make (2)

			add_image (directory + "/images/cat.png")
			add_image (directory + "/images/mouse.png")
		end

	add_image (path: READABLE_STRING_GENERAL)
		local
			image, stretched: EV_PIXMAP
		do
			create image
			image.set_with_named_file (path)
			images.extend (image)

			create stretched
			stretched_images_lru.extend (stretched)
		end

feature -- getter

	get_image (id, w, h: INTEGER): EV_PIXMAP
		local
			image, stretched: EV_PIXMAP
		do
			image := images @ id
			stretched := stretched_images_lru @ id

			if w = stretched.width and then h = stretched.height then
				Result := stretched
			elseif w = image.width and then h = image.height then
				Result := image
			else
				stretched.copy (image)
				stretched.stretch (w, h)
				Result := stretched
			end
		ensure
			correct_width: Result.width = w
			correct_height: Result.height = h
		end

end
