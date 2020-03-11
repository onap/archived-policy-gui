import $ from 'jquery';
import { ajax_get, ajax_get_statistics } from "../MonitoringUtils";

$.ajax = jest.fn().mockImplementation(() => {
    const fakeResponse = {
        data: {
            id: 1,
            name: "All",
            value: "Dummy Data"
        }
    };
    return Promise.resolve(fakeResponse);
});

test('ajax_get return ok', () => {
    ajax_get().then(response => {
        expect(response.data.id).toBe(1);
    });
});