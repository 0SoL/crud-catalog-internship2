alter table catalog drop column if exists primary_image;
alter table catalog add column primary_image_id int;

alter table if exists catalog
    add constraint fk_catalog_primary_image
    foreign key (primary_image_id)
    references image(id)
